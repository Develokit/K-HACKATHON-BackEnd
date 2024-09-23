package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.config.openAI.AssistantFeignClient;
import com.develokit.maeum_ieum.config.openAI.GptFeignClient;
import com.develokit.maeum_ieum.config.openAI.GptWebClient;
import com.develokit.maeum_ieum.config.openAI.ThreadFeignClient;
import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.dto.openAi.gpt.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.gpt.RespDto;
import com.develokit.maeum_ieum.dto.openAi.message.RespDto.ListMessageRespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import feign.FeignException;
import lombok.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import static com.develokit.maeum_ieum.dto.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.assistant.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.assistant.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.gpt.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.gpt.ReqDto.CreateGptMessageReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.gpt.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.thread.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.thread.RespDto.*;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private Logger log = LoggerFactory.getLogger(OpenAiService.class);

    private final AssistantFeignClient assistantFeignClient;
    private final ThreadFeignClient threadFeignClient;
    private final GptWebClient gptWebClient;
    private final GptFeignClient gptFeignClient;
    private final String MODEL = "gpt-4o";
    private final int MAX_TOKENS = 400;

    private final String SYSTEM_PROMPT =
            "당신은 요양사를 위한 규칙을 간결하고 실용적으로 작성하는 도우미 역할을 합니다. " +
                    "이 규칙은 당신이 앞으로 요양사가 관리하는 노인과의 대화에서 사용될 것이며, " +
                    "노인의 안전과 건강을 최우선으로 고려하여 작성해야 합니다. 불필요하게 길지 않게 작성하고, " +
                    "응답은 400 토큰 내로 작성해 주세요.";
    private final String USER_PROMPT_SUFFIX =
            "\n규칙을 구체적이고 간단하게 작성해 주세요. 최대 200자 이내로 완전한 응답을 생성해주세요.";

    //어시스턴트 생성
    public String createAssistant(OpenAiCreateAssistantReqDto openAiCreateAssistantReqDto){
        try{
            AssistantRespDto assistantRespDto = assistantFeignClient.createAssistant(
                    new OpenAiCreateAssistantReqDto(
                            MODEL,
                    openAiCreateAssistantReqDto.getName(),
                    openAiCreateAssistantReqDto.getInstructions(),
                    openAiCreateAssistantReqDto.getDescription()
                    )
            );

            log.info("Assistant created successfully. Response: {}", assistantRespDto);
            return assistantRespDto.getId();

        } catch (FeignException fe) {
            log.error("OpenAI API 호출 과정에서 에러 발생. Status: {}, Body: {}", fe.status(), fe.contentUTF8(), fe);
            throw new CustomApiException("OPENAI_SERVER_ERROR", fe.status(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e){
            log.error("AI Assistant 생성 과정에서 에러 발생 ", e);
            throw new CustomApiException("OPENAI_SERVER_ERROR", 500, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //스레드 생성
    public ThreadRespDto createThread(){ //일단 dto 안받으면서 스레드 생성
        try{
            return threadFeignClient.createThreads(new CreateThreadReqDto());
        }catch (Exception e){
            throw new CustomApiException("OPENAI_SERVER_ERROR", 500, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //메시지 리스트 조회
    public ListMessageRespDto listMessages(String threadId){
        try{
            return threadFeignClient.listMessages(threadId);
        }catch (Exception e){
            throw new CustomApiException("OPENAI_SERVER_ERROR", 500, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //어시스턴트 수정
    public AssistantRespDto modifyAssistant(String openAiAssistantId, Assistant assistant){
        try{
            return assistantFeignClient.modifyAssistant(
                    assistant.getOpenAiAssistantId(),
                    ModifyAssistantReqDto.builder()
                            .model(MODEL)
                            .name(assistant.getName())
                            .instructions(assistant.getOpenAiInstruction())
                            .description(assistant.getMandatoryRule())
                            .build()
            );
        }catch (Exception e){
            throw new CustomApiException("OPENAI_SERVER_ERROR", 500, HttpStatus.INTERNAL_SERVER_ERROR);
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
    //TODO gpt한테 필수 규칙 더 상세하게 생성해달라는 요청 -> WebFlux버전
    public Mono<AssistantMandatoryRuleRespDto> createGptMessage(AssistantMandatoryRuleReqDto assistantMandatoryRuleReqDto){
        try{
           return gptWebClient.createGptMessage(new CreateGptMessageReqDto(
                    MODEL,
                    new MessageDto(SYSTEM_PROMPT, "system"),
                    new MessageDto(assistantMandatoryRuleReqDto.getContent() + USER_PROMPT_SUFFIX, "user"),
                   MAX_TOKENS
            )).map(AssistantMandatoryRuleRespDto::new);

        }catch (Exception e){
            log.error("GPT 자동 생성 필수 규칙 반환 중 오류 발생: "+e.getMessage());
            throw new CustomApiException("OPENAI_SERVER_ERROR", 500, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    //TODO gpt한테 필수 규칙 더 상세하게 생성해달라는 요청 -> 서블릿 버전
    public AssistantMandatoryRuleRespDto createGptMessageWithFeign(AssistantMandatoryRuleReqDto assistantMandatoryRuleReqDto){
        log.debug("GPT 자동 생성 필수 규칙 요청 전송: {}", assistantMandatoryRuleReqDto.getContent());
        try{
            CreateGptMessageRespDto gptMessage = gptFeignClient.createGptMessage(new CreateGptMessageReqDto(
                    MODEL,
                    new MessageDto(SYSTEM_PROMPT, "system"),
                    new MessageDto(assistantMandatoryRuleReqDto.getContent() + USER_PROMPT_SUFFIX, "user"),
                    MAX_TOKENS)
            );
            return new AssistantMandatoryRuleRespDto(gptMessage);

        }catch (FeignException fe){
            log.error("OpenAI API 호출 과정에서 에러 발생. Status: {}, Body: {}", fe.status(), fe.contentUTF8(), fe);
            throw new CustomApiException("OPENAI_SERVER_ERROR", fe.status(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e){
            log.error("GPT 자동 생성 필수 규칙 반환 중 오류 발생: "+e.getMessage());
            throw new CustomApiException("OPENAI_SERVER_ERROR", 500, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
