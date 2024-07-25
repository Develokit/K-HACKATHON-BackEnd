package com.develokit.maeum_ieum.config.openAI;


import com.develokit.maeum_ieum.config.openAI.header.OpenAiHeaderConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.develokit.maeum_ieum.dto.openAi.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.assistant.RespDto.*;


@FeignClient(
        name = "AssistantFeignClient",
        url = "https://api.openai.com/v1/assistants",
        configuration = OpenAiHeaderConfiguration.class
)
public interface AssistantFeignClient {

    @PostMapping //어시스턴트 생성
    AssistantRespDto createAssistant(@RequestBody CreateAssistantReqDto createAssistantReqDto);
    @GetMapping //openAI 페이지네이션 옵션에 cursor for use in pagination 옵션으로 after, before가 있는데 추후 필요하면 기재하기
    ListAssistantRespDto listAssistants(@RequestParam(name="limit", defaultValue = "20")int limit,
                                          @RequestParam(name="order", defaultValue = "desc")String order);

    @GetMapping("/{assistantId}") // 어시스턴트 검색
    AssistantRespDto searchAssistant(@PathVariable("assistantId") String assistantId);

    @PostMapping("/{assistantId}") //어시스턴트 수정
    AssistantRespDto modifyAssistant(@PathVariable("assistantId") String assistantId, @RequestBody ModifyAssistantReqDto modifyAssistantReqDto);

    @DeleteMapping("/{assistantId}") //어시스턴트 삭제
    ResponseEntity<Object> deleteAssistant(@PathVariable("assistantId") String assistantId);
}
