package com.develokit.maeum_ieum.controller.elderly;

import com.develokit.maeum_ieum.dto.message.RespDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.develokit.maeum_ieum.dto.assistant.RespDto.VerifyAccessCodeRespDto;
import static com.develokit.maeum_ieum.dto.elderly.RespDto.*;
import static com.develokit.maeum_ieum.dto.emergencyRequest.ReqDto.EmergencyRequestCreateReqDto;
import static com.develokit.maeum_ieum.dto.emergencyRequest.RespDto.EmergencyRequestCreateRespDto;
import static com.develokit.maeum_ieum.dto.message.ReqDto.CreateStreamMessageReqDto;
import static com.develokit.maeum_ieum.dto.message.RespDto.*;
import static com.develokit.maeum_ieum.dto.message.RespDto.CreateStreamMessageRespDto;
import static com.develokit.maeum_ieum.dto.openAi.audio.ReqDto.CreateAudioReqDto;
import static com.develokit.maeum_ieum.dto.openAi.audio.RespDto.CreateAudioRespDto;

@Tag(name = "노인 사용자 API", description = "노인 사용자가 호출하는 API 목록")
public interface ElderlyControllerDocs {

    @Operation(summary = "노인 로그인(전달 받은 코드 입력)", description = "요양사에게 전달 받은 코드 입력할 때 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "접속 성공", content = @Content(schema = @Schema(implementation = VerifyAccessCodeRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(implementation = VerifyAccessCodeRespDto.class), mediaType = "application/json")),
    })
    ResponseEntity<?> verifyAccessCode(@PathVariable(name = "accessCode")String accessCode);

    @Operation(summary = "노인 메인 홈", description = "노인 메인 홈 진입 시 요청")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ElderlyMainRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(implementation = ElderlyMainRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND", content = @Content(schema = @Schema(implementation = ElderlyMainRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "OPENAI_SERVER_ERROR | INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ElderlyMainRespDto.class), mediaType = "application/json"))

    })
    ResponseEntity<?> mainHome(@PathVariable(name = "elderlyId")Long elderlyId, @PathVariable(name = "assistantId")Long assistantId);

    @Operation(summary = "채팅 화면 진입 시: 어시스턴트 및 스레드 검증", description = "채팅 화면 진입 시 요청")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = CheckAssistantInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(implementation = CheckAssistantInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND", content = @Content(schema = @Schema(implementation = CheckAssistantInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "OPENAI_SERVER_ERROR | INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = CheckAssistantInfoRespDto.class), mediaType = "application/json"))

    })
    ResponseEntity<?> checkAssistantInfo (@PathVariable(name = "elderlyId")Long elderlyId, @PathVariable(name = "assistantId")Long assistantId);

    @Operation(summary = "채팅 화면 진입 시: 이전 채팅 내역 가져오기", description = "이전 채팅 내역 조회 시 요청")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ChatInfoDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(implementation = ChatInfoDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND", content = @Content(schema = @Schema(implementation = ChatInfoDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "OPENAI_SERVER_ERROR | INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ChatInfoDto.class), mediaType = "application/json"))
    })
    ResponseEntity<?> getChatHistory(@PathVariable(name = "elderlyId") Long elderlyId,
                                     @RequestParam(name = "page", defaultValue = "0")int page,
                                     @RequestParam(name = "size", defaultValue = "10")int size);
    @Operation(summary = "채팅 (비스트림 답변)", description = "텍스트 기반 채팅 시 요청. 전체 텍스트 메시지 반환")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = CreateMessageRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR(긴급 알림 요양사에게 전달 실패)", content = @Content(schema = @Schema(implementation = CreateMessageRespDto.class), mediaType = "application/json"))
    })
    Mono<?> createNonStreamMessage(
            @PathVariable(name = "elderlyId")Long elderlyId,
            @RequestBody @Valid CreateStreamMessageReqDto createStreamMessageReqDto, BindingResult bindingResult);

    @Operation(summary = "채팅 (스트림 답변)", description = "텍스트 기반 채팅 시 요청. 스트림으로 메시지 반환")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = CreateStreamMessageRespDto.class), examples = {
                    @ExampleObject(
                            name = "isLast:false -> 응답 받아야 할 답변이 더 있는 상태",
                            value = "{\n  \"answer\": \"가\",\n  \"isLast\": false,\n  \"timeStamp\": null\n}",
                            summary = "답변 생성 중"
                    ),
                    @ExampleObject(
                            name = "isLast:true -> 응답 종료",
                            value = "{\n  \"answer\": \"null\",\n  \"isLast\": true,\n   \"timeStamp\": \"09.12 00:58\"\n}",
                            summary = "답변 생성 완료"
                    )
            }, mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "OPENAI_SERVER_ERROR | INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = CreateStreamMessageRespDto.class), mediaType = "application/json"))

    })
    Flux<CreateStreamMessageRespDto> createStreamMessage(
            @PathVariable(name = "elderlyId") Long elderlyId,
            @RequestBody @Valid CreateStreamMessageReqDto createStreamMessageReqDto,
            BindingResult bindingResult);


    @Operation(summary = "긴급 알림 전송", description = "연결된 요양사에게 긴급 알림 전송")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = EmergencyRequestCreateRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR(긴급 알림 요양사에게 전달 실패)", content = @Content(schema = @Schema(implementation = EmergencyRequestCreateRespDto.class), mediaType = "application/json"))

    })
    ResponseEntity<?> createEmergencyRequest(@PathVariable(name = "caregiverId")Long caregiverId,
                                             @PathVariable(name=  "elderlyId")Long elderlyId,
                                             @Valid @RequestBody EmergencyRequestCreateReqDto emergencyRequestCreateReqDto,
                                             BindingResult bindingResult);


    @Operation(summary = "채팅(오디오 답변)", description = "음성 기반 채팅 시 요청")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = CreateAudioRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = CreateAudioRespDto.class), mediaType = "application/json"))

    })
    Mono<?> createVoiceMessage(@PathVariable(name = "elderlyId")Long elderlyId,
                               @Valid @RequestBody CreateAudioReqDto createAudioReqDto,
                               BindingResult bindingResult);
}
