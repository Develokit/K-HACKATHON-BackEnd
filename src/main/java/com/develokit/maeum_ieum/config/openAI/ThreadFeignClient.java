package com.develokit.maeum_ieum.config.openAI;

import com.develokit.maeum_ieum.config.openAI.header.OpenAiHeaderConfiguration;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.message.RespDto.ListMessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.message.RespDto.MessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.run.ReqDto.CreateRunReqDto;
import com.develokit.maeum_ieum.dto.openAi.run.RespDto.RunRespDto;
import com.develokit.maeum_ieum.dto.openAi.thread.RespDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import static com.develokit.maeum_ieum.dto.openAi.message.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.thread.RespDto.*;

@FeignClient(
        name = "ThreadFeignClient",
        url = "https://api.openai.com/v1/threads",
        configuration = OpenAiHeaderConfiguration.class
)
public interface ThreadFeignClient {
    @PostMapping //스레드 생성
    ThreadRespDto createThreads();

    @GetMapping("/{threadId}") //스레드 검색
    ThreadRespDto searchThread(@PathVariable(name = "threadId")String threadId);

    @DeleteMapping("/{threadId}") //스레드 삭제
    DeleteThreadRespDto deleteThread(@PathVariable(name = "threadId")String threadId);

    @PostMapping("/{threadId}/messages") //메시지 생성
    MessageRespDto createMessages(@PathVariable("threadId") String threadId, @RequestBody CreateMessageReqDto createMessageReqDto);

    @GetMapping("/{threadId}/messages") //메시지 리스트
    ListMessageRespDto listMessages(@PathVariable("threadId") String threadId);

    @PostMapping("/{threadId}/runs") // 런 생성
    RunRespDto createRun(@PathVariable("threadId")String threadId, @RequestBody CreateRunReqDto createRunReqDto);


}
