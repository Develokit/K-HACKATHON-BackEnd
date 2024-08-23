package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.config.openAI.ThreadWebClient;
import com.develokit.maeum_ieum.controller.ElderlyController;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dto.message.ReqDto.CreateStreamMessageReqDto;
import com.develokit.maeum_ieum.dto.message.RespDto;
import com.develokit.maeum_ieum.dto.message.RespDto.CreateStreamMessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.audio.RespDto.CreateAudioRespDto;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.run.ReqDto.CreateRunReqDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import jakarta.transaction.TransactionScoped;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.develokit.maeum_ieum.dto.openAi.audio.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.message.ReqDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final ThreadWebClient threadWebClient;
    private final ElderlyRepository elderlyRepository;

    public Flux<CreateStreamMessageRespDto> getStreamMessage(CreateStreamMessageReqDto createStreamMessageReqDto, Long elderlyId){

        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 사용자 입니다. 담당 요양사에게 문의해주세요", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        return threadWebClient.createMessageAndStreamRun(
                createStreamMessageReqDto.getThreadId(),
                new CreateMessageReqDto(
                        "user",
                        createStreamMessageReqDto.getContent()
                ),
                new CreateRunReqDto(
                        createStreamMessageReqDto.getOpenAiAssistantId(),
                        true
                ),
                elderlyPS
        );
    }
    public Mono<CreateAudioRespDto> getVoiceMessage(CreateAudioReqDto createAudioReqDto, Long elderlyId){

        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 사용자 입니다. 담당 요양사에게 문의해주세요", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

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
                ),
                elderlyPS
        );
    }


}
