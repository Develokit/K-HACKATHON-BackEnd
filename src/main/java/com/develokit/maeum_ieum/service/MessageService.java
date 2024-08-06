package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.config.openAI.ThreadWebClient;
import com.develokit.maeum_ieum.controller.ElderlyController;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.dto.message.ReqDto.CreateStreamMessageReqDto;
import com.develokit.maeum_ieum.dto.message.RespDto;
import com.develokit.maeum_ieum.dto.message.RespDto.CreateStreamMessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.audio.RespDto.CreateAudioRespDto;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.run.ReqDto.CreateRunReqDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.develokit.maeum_ieum.dto.openAi.audio.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.message.ReqDto.*;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ThreadWebClient threadWebClient;

    //채팅 하기 -> 스레드 아이디, 어시스턴트 아이디, 요청 보낼 메시지 DTO 필요
    public Flux<CreateStreamMessageRespDto> getStreamMessage(CreateStreamMessageReqDto createStreamMessageReqDto){
        return threadWebClient.createMessageAndStreamRun(
                createStreamMessageReqDto.getThreadId(),
                new CreateMessageReqDto(
                        "user",
                        createStreamMessageReqDto.getContent()
                ),
                new CreateRunReqDto(
                        createStreamMessageReqDto.getOpenAiAssistantId(),
                        true
                )
        );
    }
    public Mono<CreateAudioRespDto> getVoiceMessage(CreateAudioReqDto createAudioReqDto){
        return threadWebClient.createMessageAndRun(
                createAudioReqDto.getThreadId(),
                new CreateMessageReqDto(
                        "user",
                        createAudioReqDto.getContent()
                ),
                new CreateRunReqDto(
                        createAudioReqDto.getOpenAiAssistantId(),
                        true
                ),
                new AudioRequestDto(
                        "tts-1",
                        createAudioReqDto.getGender().equals("FEMALE")?"nova":"onyx"
                )
        );
    }


}
