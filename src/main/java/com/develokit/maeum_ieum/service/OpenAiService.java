package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.config.openAI.AssistantFeignClient;
import com.develokit.maeum_ieum.config.openAI.ThreadFeignClient;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto.CreateMessageReqDto;
import com.develokit.maeum_ieum.dto.openAi.message.RespDto.MessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.run.ReqDto.CreateRunReqDto;
import com.develokit.maeum_ieum.dto.openAi.run.RespDto.StreamRunRespDto;
import com.develokit.maeum_ieum.dto.openAi.thread.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.thread.RespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.*;
import org.springframework.stereotype.Service;

import static com.develokit.maeum_ieum.dto.openAi.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.assistant.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.thread.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.thread.RespDto.*;

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

    //스레드 생성
    public ThreadRespDto createThread(){ //일단 dto 안받으면서 스레드 생성
        try{
            return threadFeignClient.createThreads(new CreateThreadReqDto());
        }catch (Exception e){
            throw new CustomApiException(e.getMessage());
        }
    }
    //메시지 생성
    public MessageRespDto createMessage(String threadId, CreateMessageReqDto createMessageReqDto){
        try{
            return threadFeignClient.createMessages(threadId,createMessageReqDto);

        }catch (Exception e){
            throw new CustomApiException(e.getMessage());
        }
    }
    //스트림 런
    public StreamRunRespDto createStreamRun(String assistantId, String threadId){
        try{
            return threadFeignClient.createRun(threadId, new CreateRunReqDto(
                    assistantId,
                    true
            ));

        }catch (Exception e){
            throw new CustomApiException(e.getMessage());
        }
    }




}
