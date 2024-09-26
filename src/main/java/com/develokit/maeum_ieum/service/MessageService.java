package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.config.openAI.ThreadWebClient;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dto.message.ReqDto.CreateStreamMessageReqDto;
import com.develokit.maeum_ieum.dto.message.RespDto;
import com.develokit.maeum_ieum.dto.message.RespDto.CreateStreamMessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.audio.RespDto.CreateAudioRespDto;
import com.develokit.maeum_ieum.dto.openAi.run.ReqDto.CreateRunReqDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static com.develokit.maeum_ieum.dto.message.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.audio.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.message.ReqDto.*;

@Service
@RequiredArgsConstructor

public class MessageService {

    private final ThreadWebClient threadWebClient;
    private final ElderlyRepository elderlyRepository;
    private final static Logger log = LoggerFactory.getLogger(MessageService.class);

    public Mono<CreateMessageRespDto> getNonStreamMessage(CreateStreamMessageReqDto createStreamMessageReqDto, Long elderlyId){
        return Mono.fromCallable(() ->
                elderlyRepository.findByIdWithAssistant(elderlyId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(elderlyOptional -> elderlyOptional
                        .map(Mono::just)
                        .orElseThrow(() -> new CustomApiException("등록되지 않은 사용자입니다. 담당 요양사에게 문의해주세요", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)))
                .flatMap(elderlyPS -> {
                    if(elderlyPS.getAssistant() == null)
                        return Mono.error(new CustomApiException("AI 어시스턴트가 등록되지 않은 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND));
                    else if(!elderlyPS.getAssistant().getOpenAiAssistantId().equals(createStreamMessageReqDto.getOpenAiAssistantId()))
                        return Mono.error(new CustomApiException("해당 사용자의 AI 어시스턴트가 아닙니다", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN));
                    else return Mono.just(elderlyPS);
                })
                .flatMap(elderlyPS ->{
                    CreateMessageReqDto createMessageReqDto = new CreateMessageReqDto(
                            "user",
                            createStreamMessageReqDto.getContent()
                    );
                    CreateRunReqDto createRunReqDto = new CreateRunReqDto(
                            createStreamMessageReqDto.getOpenAiAssistantId(),
                            true
                    );

                    return threadWebClient.createMessageAndRun(
                            createStreamMessageReqDto.getThreadId(),
                            createMessageReqDto,
                            createRunReqDto,
                            elderlyPS
                    );
                })
                .doOnError(e -> log.error("비스트림런 유저 답변 생성 중 오류 발생: ", e));
    }


    public Flux<CreateStreamMessageRespDto> getStreamMessage(CreateStreamMessageReqDto createStreamMessageReqDto, Long elderlyId){

        return Mono.fromCallable(() ->
                        elderlyRepository.findByIdWithAssistant(elderlyId))
                        .subscribeOn(Schedulers.boundedElastic()) //노인 사용자 조회 블로킹 작업 비동기로 수행
                        .flatMap(elderlyOptional -> elderlyOptional
                                .map(Mono::just)
                                .orElseThrow(() -> new CustomApiException("등록되지 않은 사용자 입니다. 담당 요양사에게 문의해주세요", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND))
                        )
                        .flatMap(elderlyPS -> {
                            if(elderlyPS.getAssistant() == null)
                                return Mono.error(new CustomApiException("AI 어시스턴트가 등록되지 않은 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND));
                            else if(!elderlyPS.getAssistant().getOpenAiAssistantId().equals(createStreamMessageReqDto.getOpenAiAssistantId()))
                                return Mono.error(new CustomApiException("해당 사용자의 AI 어시스턴트가 아닙니다", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN));
                            else return Mono.just(elderlyPS);
                        })
                        .flatMapMany(elderlyPS ->
                                threadWebClient.createMessageAndStreamRun(
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
                                )
                        );

    }

    public Mono<CreateAudioRespDto> getVoiceMessage(CreateAudioReqDto createAudioReqDto, Long elderlyId){

        return Mono.fromCallable(() ->
                        elderlyRepository.findByIdWithAssistant(elderlyId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(elderlyOptional -> elderlyOptional
                        .map(Mono::just)
                        .orElseThrow(() -> new CustomApiException("등록되지 않은 사용자입니다. 담당 요양사에게 문의해주세요", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)))
                .flatMap(elderlyPS -> {
                    if(elderlyPS.getAssistant() == null)
                        return Mono.error(new CustomApiException("AI 어시스턴트가 등록되지 않은 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND));
                    else if(!elderlyPS.getAssistant().getOpenAiAssistantId().equals(createAudioReqDto.getOpenAiAssistantId()))
                        return Mono.error(new CustomApiException("해당 사용자의 AI 어시스턴트가 아닙니다", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN));
                    else return Mono.just(elderlyPS);
                })
                .flatMap( elderlyPS ->
                        threadWebClient.createMessageAndRunForAudio(
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
                ))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    throw new CustomApiException("오디오 메시지 처리 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }


}
