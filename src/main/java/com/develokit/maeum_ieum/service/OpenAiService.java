package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.config.openAI.AssistantFeignClient;
import com.develokit.maeum_ieum.config.openAI.ThreadFeignClient;
import com.develokit.maeum_ieum.config.openAI.ThreadWebClient;
import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto.CreateMessageReqDto;
import com.develokit.maeum_ieum.dto.openAi.message.RespDto.ListMessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.message.RespDto.MessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.run.ReqDto.CreateRunReqDto;
import com.develokit.maeum_ieum.dto.openAi.run.RespDto.StreamRunRespDto;
import com.develokit.maeum_ieum.dto.openAi.thread.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.thread.RespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;



import static com.develokit.maeum_ieum.dto.openAi.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.assistant.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.thread.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.thread.RespDto.*;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private Logger log = LoggerFactory.getLogger(OpenAiService.class);

    private final AssistantFeignClient assistantFeignClient;
    private final ThreadFeignClient threadFeignClient;
    private final String model = "gpt-3.5-turbo";

    //어시스턴트 생성
    public String createAssistant(OpenAiCreateAssistantReqDto openAiCreateAssistantReqDto){
        try{
            AssistantRespDto assistantRespDto = assistantFeignClient.createAssistant(
                    new OpenAiCreateAssistantReqDto(
                    model,
                    openAiCreateAssistantReqDto.getName(),
                    openAiCreateAssistantReqDto.getInstructions(),
                    openAiCreateAssistantReqDto.getDescription()
                    )
            );
            log.debug("어시스턴트 생성!!");
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
    //메시지 리스트 조회
    public ListMessageRespDto listMessages(String threadId){
        try{
            return threadFeignClient.listMessages(threadId);
        }catch (Exception e){
            throw new CustomApiException(e.getMessage());
        }
    }

    //어시스턴트 수정
    public AssistantRespDto modifyAssistant(String openAiAssistantId, Assistant assistant){
        try{
            return assistantFeignClient.modifyAssistant(
                    assistant.getOpenAiAssistantId(),
                    ModifyAssistantReqDto.builder()
                            .model(model)
                            .name(assistant.getName())
                            .instructions(assistant.getOpenAiInstruction())
                            .description(assistant.getMandatoryRule())
                            .build()
            );
        }catch (Exception e){
            throw new CustomApiException(e.getMessage());
        }
    }

    //어시스턴트 삭제
    public void deleteAssistant(String openAiAssistantId){
        try{
            assistantFeignClient.deleteAssistant(openAiAssistantId);
        }catch (Exception e){
            throw new CustomApiException("OPENAI_SERVER_ERROR", 500, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
