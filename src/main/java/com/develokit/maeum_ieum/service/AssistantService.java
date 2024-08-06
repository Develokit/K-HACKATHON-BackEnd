package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.assistant.AssistantRepository;
import com.develokit.maeum_ieum.dto.assistant.RespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.develokit.maeum_ieum.dto.assistant.RespDto.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AssistantService {

    private final AssistantRepository assistantRepository;

    public VerifyAccessCodeRespDto verifyAccessCode(String accessCode){
        Assistant assistantPS = assistantRepository.findByAccessCode(accessCode)
                .orElseThrow(
                        () -> new CustomApiException("코드를 다시 확인해주세요")
                );

        if(assistantPS.getElderly() == null)
            throw new CustomApiException("사용할 수 없는 AI 어시서턴트 입니다");

        return new VerifyAccessCodeRespDto(assistantPS);
    }




}
