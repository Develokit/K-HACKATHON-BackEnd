package com.develokit.maeum_ieum.config.openAI;

import com.develokit.maeum_ieum.dto.openAi.gpt.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.gpt.RespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static com.develokit.maeum_ieum.dto.openAi.gpt.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.gpt.RespDto.*;

@RequiredArgsConstructor
@Component
public class GptWebClient {

    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(GptWebClient.class);

    public Mono<CreateGptMessageRespDto> createGptMessage(CreateGptMessageReqDto createGptMessageReqDto){
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(createGptMessageReqDto)
                .retrieve()
                .bodyToMono(CreateGptMessageRespDto.class)
                .doOnSubscribe(subscription -> log.info("GPT API에 요청 전송"))
                .doOnSuccess(response -> log.info("GPT API 응답 반환 완료"))
                .doOnError(WebClientResponseException.class, e -> {
                    log.error("GPT 메시지 생성 과정에서 에러 발생: " + e.getMessage());
                    throw new CustomApiException("GPT 메시지 생성 과정에서 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }
}
