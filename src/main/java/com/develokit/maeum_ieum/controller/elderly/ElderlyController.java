package com.develokit.maeum_ieum.controller.elderly;

import com.develokit.maeum_ieum.dto.message.ReqDto.CreateStreamMessageReqDto;
import com.develokit.maeum_ieum.dto.openAi.audio.RespDto.CreateAudioRespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.service.AssistantService;
import com.develokit.maeum_ieum.service.ElderlyService;
import com.develokit.maeum_ieum.service.EmergencyRequestService;
import com.develokit.maeum_ieum.service.MessageService;
import com.develokit.maeum_ieum.service.report.ReportService;
import com.develokit.maeum_ieum.util.ApiUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import static com.develokit.maeum_ieum.dto.emergencyRequest.ReqDto.*;
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
    private final ReportService reportService;

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

    //채팅 화면 진입 시 반드시 요청 -> 어시스턴트 검증 및 스레드 확인
    @GetMapping("/{elderlyId}/assistants/{assistantId}/status")
    public ResponseEntity<?> checkAssistantInfo (@PathVariable(name = "elderlyId")Long elderlyId,
                                                 @PathVariable(name = "assistantId")Long assistantId
                                                 ){
        return new ResponseEntity<>(ApiUtil.success(elderlyService.checkAssistantInfo(elderlyId, assistantId)), HttpStatus.OK);
    }

    //채팅 화면 진입 시 반드시 요청 -> 이전 채팅 내역 끌고오기
    @GetMapping("/{elderlyId}/chats")
    public ResponseEntity<?> getChatHistory(@PathVariable(name = "elderlyId") Long elderlyId,
                                                        @RequestParam(name = "page", defaultValue = "0")int page,
                                                      @RequestParam(name = "size", defaultValue = "10")int size){
        return new ResponseEntity<>(ApiUtil.success(elderlyService.getChatHistory(page, size, elderlyId)), HttpStatus.OK);
    }

    //메시지 스트림 생성
    @PostMapping("/{elderlyId}/stream-message")
    public Flux<CreateStreamMessageRespDto> createStreamMessage(
            @PathVariable(name = "elderlyId") Long elderlyId,
            @RequestBody @Valid CreateStreamMessageReqDto createStreamMessageReqDto,
                                                                BindingResult bindingResult){

        return messageService.getStreamMessage(createStreamMessageReqDto, elderlyId);
    }

    //메시지 비스트림 생성
    @PostMapping("/{elderlyId}/message")
    public Mono<?> createNonStreamMessage(
            @PathVariable(name = "elderlyId")Long elderlyId,
            @RequestBody @Valid CreateStreamMessageReqDto createStreamMessageReqDto, BindingResult bindingResult){

        return messageService.getNonStreamMessage(createStreamMessageReqDto, elderlyId)
                .map(result -> new ResponseEntity<>(ApiUtil.success(result),HttpStatus.CREATED));
    }

    @PostMapping("/{elderlyId}/voice-message")
    public Mono<?> createVoiceMessage(@PathVariable(name = "elderlyId")Long elderlyId,
                                                       @Valid @RequestBody CreateAudioReqDto createAudioReqDto,
                                                       BindingResult bindingResult){

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

    @PostMapping("/reports")
    public Mono<?> createReports(){
        return reportService.generate()
                .map(result -> new ResponseEntity<>(ApiUtil.success(result),HttpStatus.CREATED));
    }

    @PostMapping("/reports/m")
    public Mono<?> createMonthlyReports(){
        return reportService.generateMonthly()
                .map(result -> new ResponseEntity<>(ApiUtil.success(result),HttpStatus.CREATED));
    }




}
