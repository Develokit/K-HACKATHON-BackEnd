package com.develokit.maeum_ieum.controller;

import com.develokit.maeum_ieum.config.openAI.ThreadWebClient;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequest;
import com.develokit.maeum_ieum.dto.message.ReqDto.CreateStreamMessageReqDto;
import com.develokit.maeum_ieum.dto.message.RespDto;
import com.develokit.maeum_ieum.dto.openAi.audio.RespDto.CreateAudioRespDto;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto.ContentDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.service.AssistantService;
import com.develokit.maeum_ieum.service.ElderlyService;
import com.develokit.maeum_ieum.service.EmergencyRequestService;
import com.develokit.maeum_ieum.service.MessageService;
import com.develokit.maeum_ieum.util.ApiUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.slf4j.LoggerFactory;
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

import static com.develokit.maeum_ieum.dto.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.emergencyRequest.ReqDto.*;
import static com.develokit.maeum_ieum.dto.emergencyRequest.RespDto.*;
import static com.develokit.maeum_ieum.dto.message.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.audio.ReqDto.*;

@RestController
@RequestMapping("/elderlys")
@RequiredArgsConstructor
@Tag(name = "노인 사용자 API", description = "노인 사용자가 호출하는 API 목록")
public class ElderlyController implements ElderlyControllerDocs {

    private final ElderlyService elderlyService;
    private final MessageService messageService;
    private final AssistantService assistantService;
    private final EmergencyRequestService emergencyRequestService;

    //접속 코드 확인
    @GetMapping("/access-code/{accessCode}")
    public ResponseEntity<?> verifyAccessCode(@PathVariable(name = "accessCode")String accessCode){
        return new ResponseEntity<>(ApiUtil.success(assistantService.verifyAccessCode(accessCode)),HttpStatus.OK);
    }


    //메인 홈
    @GetMapping("/{elderlyId}/assistants/{assistantId}")
    public ResponseEntity<?> mainHome(@PathVariable(name = "elderlyId")Long elderlyId, @PathVariable(name = "assistantId")Long assistantId){ //db의 어시스턴트 pk
        return new ResponseEntity<>(ApiUtil.success(elderlyService.getElderlyMainInfo(elderlyId, assistantId)), HttpStatus.OK);
    }

    //채팅 화면 들어가기
    @GetMapping("/{elderlyId}/assistants/{assistantId}/chat")
    public ResponseEntity<?> checkAssistantInfo (@PathVariable(name = "elderlyId")Long elderlyId, @PathVariable(name = "assistantId")Long assistantId){
        return new ResponseEntity<>(ApiUtil.success(elderlyService.checkAssistantInfo(elderlyId, assistantId)), HttpStatus.OK);
    }

    //메시지 스트림 생성
    @PostMapping("/{elderlyId}/stream-message")
    public Flux<CreateStreamMessageRespDto> createStreamMessage(
            @PathVariable(name = "elderlyId") Long elderlyId,
            @RequestBody @Valid CreateStreamMessageReqDto createStreamMessageReqDto,
                                                                BindingResult bindingResult){
        elderlyService.updateLastChatDate(elderlyId);
        return messageService.getStreamMessage(createStreamMessageReqDto, elderlyId);
    }

    @PostMapping("/{elderlyId}/voice-message")
    public Mono<?> createVoiceMessage(@PathVariable(name = "elderlyId")Long elderlyId,
                                                       @Valid @RequestBody CreateAudioReqDto createAudioReqDto,
                                                       BindingResult bindingResult){
        elderlyService.updateLastChatDate(elderlyId);

        return messageService.getVoiceMessage(createAudioReqDto, elderlyId)
//                .flatMap(voiceMessage -> saveVoiceMessageToFile(voiceMessage, "C:\\Users\\admin\\Desktop\\maeum-ieum\\src\\main\\resources\\마음이음.mp3")
//                        .then(Mono.just(voiceMessage)))
                .map(result -> new ResponseEntity<>(ApiUtil.success(result), HttpStatus.CREATED))
                .doOnError(e -> {
                    throw new CustomApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    //createVoiceMessage 테스트를 위한 임시 코드
    public Mono<String>saveVoiceMessageToFile(CreateAudioRespDto voiceMessage, String fileName){
        return Mono.fromCallable(() -> {
            try{
                Path path = Paths.get(fileName);
                Files.write(path, voiceMessage.getAnswer());
                return "파일 경로 : " + path.toAbsolutePath().toString();
            }catch (IOException e){
                throw new CustomApiException("파일 저장 중 에러 발생",HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    //요양사에게 알림 전송
    @PostMapping("/{elderlyId}/caregivers/{caregiverId}/emergency-alerts")
    public ResponseEntity<?> createEmergencyRequest(@PathVariable(name = "caregiverId")Long caregiverId,
                                                    @PathVariable(name=  "elderlyId")Long elderlyId,
                                                    @Valid @RequestBody EmergencyRequestCreateReqDto emergencyRequestCreateReqDto,
                                                    BindingResult bindingResult){
        return new ResponseEntity<>(ApiUtil.success(emergencyRequestService.createEmergencyRequest(elderlyId, caregiverId, emergencyRequestCreateReqDto)), HttpStatus.CREATED);
    }





}
