package com.develokit.maeum_ieum.controller;

import com.develokit.maeum_ieum.dto.openAi.message.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto.ContentDto;
import com.develokit.maeum_ieum.service.ElderlyService;
import com.develokit.maeum_ieum.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/assistants")
@RequiredArgsConstructor
public class ElderlyController {

    private final ElderlyService elderlyService;

    //메인 홈
    @GetMapping("/{assistantId}")
    public ResponseEntity<?> mainHome(@PathVariable(name = "assistantId")Long assistantId){ //db의 어시스턴트 pk
        return new ResponseEntity<>(ApiUtil.success(elderlyService.mainHome(assistantId)), HttpStatus.OK);
    }
    //채팅 화면 들어가기

    //메시지 스트림 생성
    @PostMapping("/{openAiAssistantId}/threads/{threadId}")
    public Flux<String> createMessage(@PathVariable(name = "openAiAssistantId")String openAiAssistantId,
                                      @PathVariable(name = "threadId")String threadId,
                                      @RequestBody ContentDto contentDto,
                                      BindingResult bindingResult){
        return elderlyService.sendMessage(openAiAssistantId, threadId, contentDto);
    }



}
