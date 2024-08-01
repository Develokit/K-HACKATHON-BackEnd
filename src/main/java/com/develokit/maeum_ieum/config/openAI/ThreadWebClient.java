package com.develokit.maeum_ieum.config.openAI;

import com.develokit.maeum_ieum.dto.openAi.message.ReqDto.CreateMessageReqDto;
import com.develokit.maeum_ieum.dto.openAi.message.RespDto.MessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.run.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.run.RespDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import static com.develokit.maeum_ieum.dto.openAi.run.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.run.RespDto.*;

@Service
@RequiredArgsConstructor
public class ThreadWebClient {

    private final WebClient webClient;

    //메시지 생성
    public Flux<MessageRespDto> createMessage(String threadId, CreateMessageReqDto createMessageReqDto){
        return webClient.post()
                .uri("/{threadId}/messages", threadId)
                .bodyValue(createMessageReqDto)
                .retrieve()
                .bodyToFlux(MessageRespDto.class)
                .doOnError(WebClientResponseException.class, e -> {
                    System.err.println("에러 코드: " + e.getStatusCode());
                    System.err.println("에러 응답 본문: " + e.getResponseBodyAsString());
                });
    }

    //스트림 런 생성
    public Flux<String> createStreamRun(String threadId, CreateRunReqDto createRunReqDto){
        return webClient.post()
                .uri("/{threadId}/runs", threadId)
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
                            return textNode.path("value").asText();
                        }
                        return "";
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return "";
                    }
                });
    }

    //메시지 생성 후 스트림 런 작업 처리
    public Flux<String> createMessageAndStreamRun(String threadId, CreateMessageReqDto createMessageReqDto, CreateRunReqDto createRunReqDto){
        return createMessage(threadId, createMessageReqDto)
                .thenMany(createStreamRun(threadId, createRunReqDto));

    }
}
