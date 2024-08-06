package com.develokit.maeum_ieum.controller;

import com.develokit.maeum_ieum.config.openAI.ThreadWebClient;
import com.develokit.maeum_ieum.dto.message.ReqDto.CreateStreamMessageReqDto;
import com.develokit.maeum_ieum.dto.message.RespDto;
import com.develokit.maeum_ieum.dto.openAi.audio.RespDto.CreateAudioRespDto;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto.ContentDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.service.AssistantService;
import com.develokit.maeum_ieum.service.ElderlyService;
import com.develokit.maeum_ieum.service.MessageService;
import com.develokit.maeum_ieum.util.ApiUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.develokit.maeum_ieum.dto.message.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.audio.ReqDto.*;

@RestController
@RequestMapping("/elderlys")
@RequiredArgsConstructor
public class ElderlyController {

    private final ElderlyService elderlyService;
    private final MessageService messageService;
    private final AssistantService assistantService;

    //접속 코드 확인
    @GetMapping
    public ResponseEntity<?> verifyAccessCode(@RequestParam(name = "access-code")String accessCode){
        return new ResponseEntity<>(ApiUtil.success(assistantService.verifyAccessCode(accessCode)),HttpStatus.OK);
    }

    //메인 홈
    @GetMapping("/{elderlyId}/assistants/{assistantId}")
    public ResponseEntity<?> mainHome(@PathVariable(name = "elderlyId")Long elderlyId, @PathVariable(name = "assistantId")Long assistantId){ //db의 어시스턴트 pk
        return new ResponseEntity<>(ApiUtil.success(elderlyService.mainHome(elderlyId, assistantId)), HttpStatus.OK);
    }
    //채팅 화면 들어가기



    //메시지 스트림 생성
    @PostMapping("/{elderlyId}/stream-message")
    public Flux<CreateStreamMessageRespDto> createStreamMessage(
            @PathVariable(name = "elderlyId") Long elderlyId,
            @RequestBody CreateStreamMessageReqDto createStreamMessageReqDto,
                                                                BindingResult bindingResult){
        elderlyService.updateLastChatDate(elderlyId);
        return messageService.getStreamMessage(createStreamMessageReqDto);
    }

    @PostMapping("/{elderlyId}/voice-message")
    public Mono<?> createVoiceMessage(@PathVariable(name = "elderlyId")Long elderlyId,
                                                       @Valid @RequestBody CreateAudioReqDto createAudioReqDto,
                                                       BindingResult bindingResult){
        elderlyService.updateLastChatDate(elderlyId);

        return messageService.getVoiceMessage(createAudioReqDto)
//                .flatMap(voiceMessage -> saveVoiceMessageToFile(voiceMessage, "C:\\Users\\admin\\Desktop\\maeum-ieum\\src\\main\\resources\\마음이음.mp3")
//                        .then(Mono.just(voiceMessage)))
                .map(result -> new ResponseEntity<>(ApiUtil.success(result), HttpStatus.CREATED))
                .doOnError(e -> {
                    System.err.println("에러 발생: " + e.getMessage());
                    throw new CustomApiException(e.getMessage());
                });
    }

    public Mono<String>saveVoiceMessageToFile(CreateAudioRespDto voiceMessage, String fileName){
        return Mono.fromCallable(() -> {
            try{
                Path path = Paths.get(fileName);
                Files.write(path, voiceMessage.getAnswer());
                return "file Path : " + path.toAbsolutePath().toString();
            }catch (IOException e){
                throw new RuntimeException("Error while saving file", e);
            }
        });
    }





}
