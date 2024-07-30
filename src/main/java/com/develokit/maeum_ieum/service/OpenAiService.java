package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.config.openAI.AssistantFeignClient;
import com.develokit.maeum_ieum.config.openAI.ThreadFeignClient;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.*;
import org.springframework.stereotype.Service;

import static com.develokit.maeum_ieum.dto.openAi.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.assistant.RespDto.*;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final AssistantFeignClient assistantFeignClient;
    private final ThreadFeignClient threadFeignClient;
    private String model = "gpt-3.5-turbo";

    //어시스턴트 생성
    public String createAssistant(OpenAiCreateAssistantReqDto openAiCreateAssistantReqDto){
        try{
            AssistantRespDto assistantRespDto = assistantFeignClient.createAssistant(
                    new OpenAiCreateAssistantReqDto(
                    model,
                    openAiCreateAssistantReqDto.getDescription(),
                    openAiCreateAssistantReqDto.getInstructions(),
                    openAiCreateAssistantReqDto.getName()
                    )
            );
            System.out.println("===어시스턴트 생성!!===");
            return assistantRespDto.getId();

        }catch (Exception e){
            throw new CustomApiException(e.getMessage());
        }
    }




}
