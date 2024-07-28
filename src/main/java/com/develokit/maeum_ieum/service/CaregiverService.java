package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.assistant.AssistantRepository;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dto.caregiver.RespDto;
import com.develokit.maeum_ieum.dto.caregiver.RespDto.JoinRespDto;
import com.develokit.maeum_ieum.dto.openAi.assistant.ReqDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.develokit.maeum_ieum.dto.caregiver.ReqDto.*;
import static com.develokit.maeum_ieum.dto.caregiver.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.service.OpenAiService.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaregiverService {

    private final CareGiverRepository careGiverRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final S3Service s3Service;
    private final OpenAiService openAiService;
    private final ElderlyRepository elderlyRepository;
    private final AssistantRepository assistantRepository;

    @Transactional
    public JoinRespDto join(JoinReqDto joinReqDto){ //회원가입

        //아이디 중복 검사
        Optional<Caregiver> caregiverOP = careGiverRepository.findByUsername(joinReqDto.getUsername());
        if(caregiverOP.isPresent()) throw new CustomApiException("이미 존재하는 아이디 입니다");

        //이미지 저장
        String imgUrl = null;
        if(joinReqDto.getImg() != null)
            imgUrl = s3Service.uploadImage(joinReqDto.getImg());

        //비밀번호 인코딩
        Caregiver caregiverPS = careGiverRepository.save(joinReqDto.toEntity(bCryptPasswordEncoder, imgUrl));

        //DTO 반환
        return new JoinRespDto(caregiverPS);
    }

    @Transactional //노인 사용자의 AI Assistant 생성
    public CreateAssistantRespDto attachAssistantToElderly(CreateAssistantReqDto  createAssistantReqDto, Long elderlyId, Caregiver caregiver){

        //존재하는 전문가인지 검사
//        careGiverRepository.findByUsername(caregiver.getUsername())
//                .orElseThrow(() -> new CustomApiException("등록 권한이 없습니다"));

        Optional<Caregiver> caregiverOP = careGiverRepository.findByUsername(caregiver.getUsername());
        if(caregiverOP.isEmpty()) throw new CustomApiException("등록 권한이 없습니다");


        //존재하는 사용자인지 검사
        Elderly elderlyPS = elderlyRepository.findById(elderlyId)
                .orElseThrow(() -> new CustomApiException("존재하지 않는 어르신 입니다"));


        //해당 사용자가 이미 어시스턴트가 붙어 있는지 검사
        if(elderlyPS.hasAssistant()) throw new CustomApiException("이미 AI Assistant가 생성되어 있습니다");


        //필수 속성과 선택 속성(대화 주제, 금기어, 응답 형식, 성격) 설정하기
        String instructions = "";

        //필수 속성
        instructions += createAssistantReqDto.getDescription()+"\n";


        //선택 속성
        if(createAssistantReqDto.getConversationTopic() != null){ //대화 주제
            instructions += "당신은 나와 "+createAssistantReqDto.getConversationTopic()+"을 주제로 얘기합니다. ";
        }
        if(createAssistantReqDto.getForbiddenTopic() != null){ //금기어
            instructions += "당신은 '"+createAssistantReqDto.getForbiddenTopic()+"'에 관해서는 절대 언급해서는 안됩니다!!! ";
        }
        if(createAssistantReqDto.getResponseType() != null){ //응답 형식
            instructions += "나와 대화할 때 '"+createAssistantReqDto.getResponseType()+"'의 응답 형식을 지켜서 대화해주세요. ";
        }
        if(createAssistantReqDto.getPersonality() != null){ //성격
            instructions += "당신의 성격은 '"+createAssistantReqDto.getPersonality()+"' 합니다!";
        }

        //instructions 설정
        createAssistantReqDto.setInstructions(instructions);

        //어시스턴트 생성
        String assistantId = openAiService.createAssistant(createAssistantReqDto);

        //어시스턴트 저장
        Assistant assistantPS = assistantRepository.save(
                Assistant.builder()
                        .name(createAssistantReqDto.getName())
                        .openAiAssistantId(assistantId)
                        .caregiver(caregiver)
                        .elderly(elderlyPS)
                        .rule(createAssistantReqDto.getDescription())
                        .conversationTopic(createAssistantReqDto.getConversationTopic())
                        .responseType(createAssistantReqDto.getResponseType())
                        .personality(createAssistantReqDto.getPersonality())
                        .forbiddenTopic(createAssistantReqDto.getForbiddenTopic())
                        .build()
        );

        //노인에 어시스턴트 주입
        elderlyPS.attachAssistant(assistantPS);

        return new CreateAssistantRespDto(assistantPS, instructions);
    }

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Builder
    public static class CreateAssistantRespDto{
        private String assistantId;
        private String name;
        private String description;
        private String instructions;
        public CreateAssistantRespDto(Assistant assistant, String instructions){
            this.assistantId = assistant.getOpenAiAssistantId();
            this.name = assistant.getName();
            this.description = assistant.getRule();
            this.instructions = instructions;
        }
    }

    //홈 화면 (요양사 이름, 이미지, 총 관리 인원, 기관, 노인 사용자(이름, 나이, 주소, 연락처, 마지막 방문(n시간 전), 마지막 대화(n시간 전)), AI 붙어있는 거



    public MyInfoRespDto caregiverInfo(String username){//내 정보(이름, 이미지, 성별, 생년월일, 주거지, 소속기관, 연락처)

        //요양사 검증
        Caregiver caregiverPS = careGiverRepository.findByUsername(username)
                .orElseThrow(
                        () -> new CustomApiException("존재하지 않는 정보입니다")
                );

        return new MyInfoRespDto(caregiverPS);
    }







}
