package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.config.openAI.ThreadWebClient;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.run.ReqDto.CreateRunReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static com.develokit.maeum_ieum.dto.openAi.message.ReqDto.*;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ThreadWebClient threadWebClient;

    public Flux<String> getStreamMessage(String threadId, CreateMessageReqDto createMessageReqDto, CreateRunReqDto createRunReqDto){
        return threadWebClient.createMessageAndStreamRun(threadId, createMessageReqDto,createRunReqDto);
    }

}
