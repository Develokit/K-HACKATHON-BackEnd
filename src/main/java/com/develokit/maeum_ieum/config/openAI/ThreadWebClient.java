package com.develokit.maeum_ieum.config.openAI;

import com.develokit.maeum_ieum.dto.message.RespDto.CreateStreamMessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.audio.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.audio.RespDto.CreateAudioRespDto;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto.CreateMessageReqDto;
import com.develokit.maeum_ieum.dto.openAi.message.RespDto.MessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.run.RespDto;
import com.develokit.maeum_ieum.dto.openAi.run.RespDto.StreamRunRespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.develokit.maeum_ieum.dto.openAi.audio.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.run.ReqDto.*;


@Service
@RequiredArgsConstructor
public class ThreadWebClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    //메시지 생성
    public Flux<MessageRespDto> createMessage(String threadId, CreateMessageReqDto createMessageReqDto){
        return webClient.post()
                .uri("/threads/{threadId}/messages", threadId)
                .bodyValue(createMessageReqDto)
                .retrieve()
                .bodyToFlux(MessageRespDto.class)
                .doOnError(WebClientResponseException.class, e -> {
                    System.err.println("에러 코드: " + e.getStatusCode());
                    System.err.println("에러 응답 본문: " + e.getResponseBodyAsString());
                });
    }

    public Mono<MessageRespDto> createSingleMessage(String threadId, CreateMessageReqDto createMessageReqDto){
        System.out.println("===메시지 생성 함수 시작===");
        return webClient.post()
                .uri("/threads/{threadId}/messages",threadId)
                .bodyValue(createMessageReqDto)
                .retrieve()
                .bodyToMono(MessageRespDto.class)
                .doOnError(WebClientResponseException.class, e -> {
                    System.err.println("에러 코드: " + e.getStatusCode());
                    System.err.println("에러 응답 본문: " + e.getResponseBodyAsString());
                });
    }

    //스트림 런 생성
    public Flux<CreateStreamMessageRespDto> createStreamRun(String threadId, CreateRunReqDto createRunReqDto){
        return webClient.post()
                .uri("/threads/{threadId}/runs", threadId)
                .bodyValue(createRunReqDto)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                .doOnError(WebClientResponseException.class, e -> {
                    System.err.println("에러 코드: " + e.getStatusCode());
                    System.err.println("에러 응답 본문: " + e.getResponseBodyAsString());
                })
                .filter(event -> "thread.message.delta".equals(event.event()))
                .map(event -> {
                    String data = event.data();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(data);
                        JsonNode deltaNode = rootNode.path("delta");
                        JsonNode contentArray = deltaNode.path("content");
                        if (contentArray.isArray() && contentArray.size() > 0) {
                            JsonNode textNode = contentArray.get(0).path("text");
                            String answer = textNode.path("value").asText();
                            return new CreateStreamMessageRespDto(answer);
                        }
                        return null; //TODO 이거 처리할것!!!
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return null; //TODO 이거 처리할것!!!
                    }
                });
    }

    //메시지 생성 후 스트림 런 작업 처리
    public Flux<CreateStreamMessageRespDto> createMessageAndStreamRun(String threadId, CreateMessageReqDto createMessageReqDto, CreateRunReqDto createRunReqDto){
        return createMessage(threadId, createMessageReqDto)
                .thenMany(createStreamRun(threadId, createRunReqDto));

    }

    //비스트림 런 생성 (생성된 전체 답변)
    Mono<String> createRun(String threadId, CreateRunReqDto createRunReqDto){
        return webClient.post()
                .uri("/threads/{threadId}/runs", threadId)
                .bodyValue(createRunReqDto)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {}) //SSE 스트림
                .doOnError(WebClientResponseException.class, e -> {
                    System.err.println("에러 코드: " + e.getStatusCode());
                    System.err.println("에러 응답 본문: " + e.getResponseBodyAsString());
                    throw new CustomApiException(e.getMessage());
                })
                .filter(event -> "thread.message.completed".equals(event.event()))
                .next()
                .flatMap(event -> {
                    String data = event.data();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(data);
                        JsonNode contentArray = rootNode.path("content");
                        if (contentArray.isArray() && contentArray.size() > 0) {
                            JsonNode textNode = contentArray.get(0).path("text");
                            System.out.println(textNode.path("value").asText());
                            return Mono.just(textNode.path("value").asText());
                        }
                        return Mono.just("");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return Mono.error(e);
                    }
                })
                .doOnNext(text -> System.out.println("text = " + text));
    }

    //오디오 생성
    public Mono<CreateAudioRespDto> createAudio(AudioRequestDto audioRequestDto){
        return webClient.post()
                .uri("/audio/speech")
                .bodyValue(audioRequestDto)
                .retrieve()
                .bodyToMono(byte[].class)
                .map(bytes -> new CreateAudioRespDto(bytes))
                .doOnError(WebClientResponseException.class, e -> {
                    System.err.println("에러 코드: " + e.getStatusCode());
                    System.err.println("에러 응답 본문: " + e.getResponseBodyAsString());
                });
    }

    // 메시지 생성 -> 답변 생성 -> 답변을 오디오로 변환
    public Mono<CreateAudioRespDto> createMessageAndRun(String threadId, CreateMessageReqDto createMessageReqDto, CreateRunReqDto createRunReqDto, AudioRequestDto audioRequestDto){
        return createSingleMessage(threadId, createMessageReqDto)
                .then(createRun(threadId, createRunReqDto))
                .flatMap(text -> {
                    audioRequestDto.setInput(text);
                    return createAudio(audioRequestDto);
                });
    }





}
