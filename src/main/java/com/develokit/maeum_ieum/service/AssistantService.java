package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.assistant.AssistantRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dto.openAi.assistant.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.assistant.RespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.util.CustomAccessCodeGenerator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.develokit.maeum_ieum.dto.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.assistant.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.assistant.RespDto.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AssistantService {

    private final AssistantRepository assistantRepository;
    private final OpenAiService openAiService;
    private final ElderlyRepository elderlyRepository;
    private final CareGiverRepository careGiverRepository;
    private final CustomAccessCodeGenerator accessCodeGenerator;

    public VerifyAccessCodeRespDto verifyAccessCode(String accessCode){

        Elderly elderlyPS = elderlyRepository.findByAccessCode(accessCode)
                .orElseThrow(
                        () -> new CustomApiException("코드를 다시 확인해주세요", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN)
                );

        if(elderlyPS.getAssistant() == null){
            throw new CustomApiException("AI 어시스턴트가 존재하지 않습니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
        }

        return new VerifyAccessCodeRespDto(elderlyPS);
    }

    //TODO 어시스턴트 수정
    @Transactional
    public AssistantModifyRespDto modifyAssistantInfo(AssistantModifyReqDto assistantModifyReqDto, Long elderlyId, Long assistantId){
        //어시스턴트 가져오기
        Assistant assistantPS = assistantRepository.findById(assistantId)
                .orElseThrow(
                        () -> new CustomApiException("AI 어시스턴트가 존재하지 않습니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
                );
        //db 정보 수정
        assistantPS.update(assistantModifyReqDto);

        openAiService.modifyAssistant(assistantPS.getOpenAiAssistantId(), assistantPS);

        return new AssistantModifyRespDto(assistantPS);
    }

    //TODO 어시스턴트 삭제
    @Transactional
    public AssistantDeleteRespDto deleteAssistant(Long assistantId, Long elderlyId, String username){
        //노인 사용자와 연결 끊기
        Elderly elderlyPS = elderlyRepository.findById(elderlyId)
                .orElseThrow(() -> new CustomApiException("존재하지 않는 노인 사용자 입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
                );

        if(!elderlyPS.hasAssistant()) throw new CustomApiException("AI 어시스턴트가 존재하지 않습니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
        elderlyPS.deleteAssistant();
        //접근 코드 삭제
        elderlyPS.deleteAccessCode();

        Assistant assistantPS = assistantRepository.findById(assistantId).orElseThrow(
                () -> new CustomApiException("AI 어시스턴트가 존재하지 않습니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //요양사와 연결 끊기
        Caregiver caregiverPS = careGiverRepository.findByUsername(username).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );
        caregiverPS.getAssistantList().remove(assistantPS);

        //openAI 어시스턴트 삭제
        openAiService.deleteAssistant(assistantPS.getOpenAiAssistantId());

        //어시스턴트 삭제
        assistantRepository.delete(assistantPS);

        return new AssistantDeleteRespDto(elderlyPS);
    }

    @Transactional //노인 사용자의 AI Assistant 생성
    public CreateAssistantRespDto attachAssistantToElderly(CreateAssistantReqDto createAssistantReqDto, Long elderlyId, String username){

        Caregiver caregiverPS = careGiverRepository.findByUsername(username).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //존재하는 사용자인지 검사
        Elderly elderlyPS = elderlyRepository.findById(elderlyId)
                .orElseThrow(() -> new CustomApiException("존재하지 않는 노인 사용자 입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND));


        //해당 사용자가 이미 어시스턴트가 붙어 있는지 검사
        if(elderlyPS.hasAssistant()) throw new CustomApiException("이미 AI Assistant가 생성되어 있습니다", HttpStatus.CONFLICT.value(),HttpStatus.CONFLICT);


        //필수 속성과 선택 속성(대화 주제, 금기어, 응답 형식, 성격) 설정하기
        String instructions = "";

        //필수 속성
        instructions += createAssistantReqDto.getMandatoryRule()+"\n";


        //선택 속성
        if(createAssistantReqDto.getConversationTopic() != null){ //대화 주제
            instructions += "[대화 주제 : 당신은"+createAssistantReqDto.getConversationTopic()+"을 주제로 얘기합니다.]\n";
        }
        if(createAssistantReqDto.getForbiddenTopic() != null){ //금기어
            instructions += "[금기어 및 금기 주제 : '"+createAssistantReqDto.getForbiddenTopic()+"']\n";
        }
        if(createAssistantReqDto.getResponseType() != null){ //응답 형식
            instructions += "[준수 응답 형식 : '"+createAssistantReqDto.getResponseType()+"']\n";
        }
        if(createAssistantReqDto.getPersonality() != null){ //성격
            instructions += "[성격 : 당신의 성격은 '"+createAssistantReqDto.getPersonality()+"']";
        }


        //어시스턴트 생성 + instructions 설정
        String assistantId = openAiService.createAssistant(
                ReqDto.OpenAiCreateAssistantReqDto.builder()
                        .description(createAssistantReqDto.getMandatoryRule())
                        .instructions(instructions)
                        .name(createAssistantReqDto.getName())
                        .build()
        );

        //어시스턴트 저장
        Assistant assistantPS = assistantRepository.save(
                Assistant.builder()
                        .name(createAssistantReqDto.getName())
                        .openAiAssistantId(assistantId)
                        .caregiver(caregiverPS)
                        .mandatoryRule(createAssistantReqDto.getMandatoryRule())
                        .conversationTopic(createAssistantReqDto.getConversationTopic())
                        .responseType(createAssistantReqDto.getResponseType())
                        .personality(createAssistantReqDto.getPersonality())
                        .forbiddenTopic(createAssistantReqDto.getForbiddenTopic())
                        .openAiInstruction(instructions)
                        .build()
        );

        //노인에 어시스턴트 + accessCode 주입
        elderlyPS.attachAssistant(assistantPS);
        String accessCode = accessCodeGenerator.generateEncodedAccessCode(elderlyPS.getName());
        elderlyPS.attachAccessCode(accessCode);

        return new CreateAssistantRespDto(assistantPS, accessCode);
    }

    public AssistantInfoRespDto getAssistantInfo(Long elderlyId, Long assistantId, String username){

        //존재하는 사용자인지 검사
        Elderly elderlyPS = elderlyRepository.findById(elderlyId)
                .orElseThrow(() -> new CustomApiException("존재하지 않는 노인 사용자 입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND));

        //노인 사용자가 현재 접속한 요양사의 관리 대상인지 검사
        if(!elderlyPS.getCaregiver().getUsername().equals(username))
            throw new CustomApiException("관리 대상이 아닙니다", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN);

        //어시스턴트 가져오기
        Assistant assistantPS = assistantRepository.findById(assistantId)
                .orElseThrow(
                        () -> new CustomApiException("AI 어시스턴트가 존재하지 않습니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
                );

        return new AssistantInfoRespDto(assistantPS);
    }







}
