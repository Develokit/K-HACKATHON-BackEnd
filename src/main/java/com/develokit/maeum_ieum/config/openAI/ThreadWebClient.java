package com.develokit.maeum_ieum.config.openAI;

import com.develokit.maeum_ieum.domain.message.Message;
import com.develokit.maeum_ieum.domain.message.MessageRepository;
import com.develokit.maeum_ieum.domain.message.MessageType;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.dto.message.RespDto.CreateStreamMessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.audio.RespDto.CreateAudioRespDto;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto.CreateMessageReqDto;
import com.develokit.maeum_ieum.dto.openAi.message.RespDto.MessageRespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.jmx.export.annotation.ManagedNotifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static com.develokit.maeum_ieum.dto.openAi.audio.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.run.ReqDto.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThreadWebClient {

    private final WebClient webClient;

    private final MessageRepository messageRepository;
    private static final Logger logger = LoggerFactory.getLogger(ThreadWebClient.class);

    //메시지 생성
    public Flux<MessageRespDto> createMessage(String threadId, CreateMessageReqDto createMessageReqDto){
        return webClient.post()
                .uri("/threads/{threadId}/messages", threadId)
                .bodyValue(createMessageReqDto)
                .retrieve()
                .bodyToFlux(MessageRespDto.class)
                .doOnError(WebClientResponseException.class, e -> {
                    logger.error(e.getMessage());
                    throw new CustomApiException("메시지 생성 과정에서 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    public Mono<MessageRespDto> createSingleMessage(String threadId, CreateMessageReqDto createMessageReqDto){
        return webClient.post()
                .uri("/threads/{threadId}/messages",threadId)
                .bodyValue(createMessageReqDto)
                .retrieve()
                .bodyToMono(MessageRespDto.class)
                .doOnError(WebClientResponseException.class, e -> {
                    logger.error(e.getMessage());
                    throw new CustomApiException("메시지 생성 과정에서 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }
    @Transactional
    public Flux<CreateStreamMessageRespDto> createStreamRun(String threadId, CreateRunReqDto createRunReqDto, Elderly elderly, CreateMessageReqDto createMessageReqDto) {
        return webClient.post()
                .uri("/threads/{threadId}/runs", threadId)
                .bodyValue(createRunReqDto)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
                })
                .doOnError(WebClientResponseException.class, e -> {
                    logger.error(e.getMessage());
                    throw new CustomApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .filter(event -> "thread.message.delta".equals(event.event()) ||  "thread.message.completed".equals(event.event()))
                .publishOn(Schedulers.boundedElastic())
                .handle((event, sink) -> {
                            String data = event.data();
                            try {
                                ObjectMapper om = new ObjectMapper();
                                JsonNode rootNode = om.readTree(data);

                                if ("thread.message.delta".equals(event.event())) {
                                    JsonNode deltaNode = rootNode.path("delta");
                                    JsonNode contentArray = deltaNode.path("content");
                                    if (!contentArray.isEmpty()) {
                                        JsonNode textNode = contentArray.get(0).path("text");
                                        String answer = textNode.path("value").asText();
                                        sink.next(new CreateStreamMessageRespDto(answer, false, null));
                                        return;
                                    }
                                } else if ("thread.message.completed".equals(event.event())) {
                                    JsonNode contentArray = rootNode.path("content");
                                    if (!contentArray.isEmpty()) {
                                        JsonNode textNode = contentArray.get(0).path("text");
                                        String answer = textNode.path("value").asText();
                                        //유저 질문 저장
                                        messageRepository.save(Message.builder()
                                                .elderly(elderly)
                                                .content(createMessageReqDto.getContent())
                                                .messageType(MessageType.USER)
                                                .build());
                                        //어시스턴트 답변 저장
                                        Message aiMessage = messageRepository.save(Message.builder()
                                                .messageType(MessageType.AI)
                                                .elderly(elderly)
                                                .content(answer)
                                                .build());
                                        sink.next(new CreateStreamMessageRespDto(null, true, CustomUtil.LocalDateTimeFormatForChatResponse(aiMessage.getCreatedDate())));
                                        sink.complete();
                                        return;
                                    }
                                    sink.error(new CustomApiException("답변 생성 과정에서 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR));
                                }
                            } catch (JsonProcessingException e) {
                                logger.error(e.getMessage());
                                sink.error(new CustomApiException("답변 직렬화 과정에서 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR));
                            }
                        });
    }


    //유저 메시지 저장 -> 메시지 생성 -> 스트림 런 작업 처리
    public Flux<CreateStreamMessageRespDto> createMessageAndStreamRun(String threadId, CreateMessageReqDto createMessageReqDto, CreateRunReqDto createRunReqDto, Elderly elderly){
        return createMessage(threadId, createMessageReqDto)
                .thenMany(createStreamRun(threadId, createRunReqDto, elderly, createMessageReqDto));

    }


    //비스트림 런 생성 (생성된 전체 답변)
    Mono<String> createRun(String threadId, CreateRunReqDto createRunReqDto){
        return webClient.post()
                .uri("/threads/{threadId}/runs", threadId)
                .bodyValue(createRunReqDto)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {}) //SSE 스트림
                .doOnError(WebClientResponseException.class, e -> {
                    logger.error(e.getMessage());
                    throw new CustomApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .filter(event -> "thread.message.completed".equals(event.event()))
                .next()
                .flatMap(event -> {
                    String data = event.data();
                    try {
                        ObjectMapper om = new ObjectMapper();
                        JsonNode rootNode = om.readTree(data);
                        JsonNode contentArray = rootNode.path("content");
                        if (!contentArray.isEmpty()) {
                            JsonNode textNode = contentArray.get(0).path("text");
                            return Mono.just(textNode.path("value").asText());
                        }
                        return Mono.just("");
                    } catch (JsonProcessingException e) {
                        logger.error(e.getMessage());
                        return Mono.error(new CustomApiException("답변 생성 과정에서 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR));
                    }
                });
    }

    //오디오 생성
    public Mono<CreateAudioRespDto> createAudio(AudioRequestDto audioRequestDto, LocalDateTime aiMessageCreateTime){
        return webClient.post()
                .uri("/audio/speech")
                .bodyValue(audioRequestDto)
                .retrieve()
                .bodyToMono(byte[].class)
                .map(audio -> new CreateAudioRespDto(audio, audioRequestDto.getInput(), CustomUtil.LocalDateTimeFormatForChatResponse(aiMessageCreateTime)))
                .doOnError(WebClientResponseException.class, e -> {
                    logger.error(e.getMessage());
                    throw new CustomApiException("오디오 생성 과정에서 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    // 메시지 생성 -> 답변 생성 -> 유저 질문 & ai 응답 저장 -> 답변을 오디오로 변환
    @Transactional
    public Mono<CreateAudioRespDto> createMessageAndRun(String threadId, CreateMessageReqDto createMessageReqDto, CreateRunReqDto createRunReqDto, AudioRequestDto audioRequestDto, Elderly elderly){
        return createSingleMessage(threadId, createMessageReqDto)
                .then(createRun(threadId, createRunReqDto))
                .flatMap(text ->
                    Mono.fromCallable(() ->
                    {
                        audioRequestDto.setInput(text);

                        Message userMessage = messageRepository.save(Message.builder()
                                .messageType(MessageType.USER)
                                .content(createMessageReqDto.getContent())
                                .elderly(elderly)
                                .build());

                        return messageRepository.save(Message.builder()
                                .messageType(MessageType.AI)
                                .content(text)
                                .elderly(elderly)
                                .build());
                    })
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(aiMessage -> createAudio(audioRequestDto, aiMessage.getCreatedDate()))
                );
    }





}
