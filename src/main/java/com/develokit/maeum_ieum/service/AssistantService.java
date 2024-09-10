package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.controller.ElderlyController;
import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.assistant.AssistantRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dto.assistant.ReqDto;
import com.develokit.maeum_ieum.dto.assistant.RespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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

    public VerifyAccessCodeRespDto verifyAccessCode(String accessCode){

        Assistant assistantPS = assistantRepository.findByAccessCode(accessCode)
                .orElseThrow(
                        () -> new CustomApiException("코드를 다시 확인해주세요", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN)
                );

        if(assistantPS.getElderly() == null)
            throw new CustomApiException("AI 어시스턴트가 존재하지 않습니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);

        return new VerifyAccessCodeRespDto(assistantPS);
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





}
