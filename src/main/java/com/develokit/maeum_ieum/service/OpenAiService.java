package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.config.openAI.AssistantFeignClient;
import com.develokit.maeum_ieum.config.openAI.ThreadFeignClient;
import com.develokit.maeum_ieum.dto.openAi.assistant.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.assistant.ReqDto.CreateAssistantReqDto;
import com.develokit.maeum_ieum.dto.openAi.assistant.RespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.*;
import org.springframework.stereotype.Service;

import static com.develokit.maeum_ieum.dto.openAi.assistant.RespDto.*;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final AssistantFeignClient assistantFeignClient;
    private final ThreadFeignClient threadFeignClient;
    private String model = "gpt-3.5-turbo";

    //어시스턴트 생성
    public String createAssistant(CreateAssistantReqDto createAssistantReqDto){
        try{
            AssistantRespDto assistantRespDto = assistantFeignClient.createAssistant(
                    new CreateAssistantReqDto(
                            model,
                            createAssistantReqDto.getName(),
                            createAssistantReqDto.getDescription(),
                            createAssistantReqDto.getInstructions()
                    )
            );
            return assistantRespDto.getId();

        }catch (Exception e){
            throw new CustomApiException(e.getMessage());
        }
    }




}
