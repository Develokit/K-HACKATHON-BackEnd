package com.develokit.maeum_ieum.controller;

import com.develokit.maeum_ieum.dto.assistant.ReqDto;
import com.develokit.maeum_ieum.dto.elderly.RespDto;
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

import static com.develokit.maeum_ieum.dto.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.assistant.RespDto.*;
import static com.develokit.maeum_ieum.dto.elderly.RespDto.*;
import static com.develokit.maeum_ieum.dto.message.ReqDto.*;

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

    @Operation(summary = "채팅 화면 진입", description = "채팅 화면 진입 시 요청")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = CheckAssistantInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(implementation = CheckAssistantInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND", content = @Content(schema = @Schema(implementation = CheckAssistantInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "OPENAI_SERVER_ERROR | INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = CheckAssistantInfoRespDto.class), mediaType = "application/json"))

    })
    ResponseEntity<?> checkAssistantInfo (@PathVariable(name = "elderlyId")Long elderlyId, @PathVariable(name = "assistantId")Long assistantId);


    @Operation(summary = "채팅 (스트림 답변)", description = "텍스트 기반 채팅 시 요청. 스트림으로 메시지 반환")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = CheckAssistantInfoRespDto.class), examples = {
                    @ExampleObject(
                            name = "isLast:false -> 응답 받아야 할 답변이 더 있는 상태",
                            value = "{\n  \"answer\": \"가\",\n  \"isLast\": false\n}",
                            summary = "답변 생성 중"
                    ),
                    @ExampleObject(
                            name = "isLast:true -> 응답 종료",
                            value = "{\n  \"answer\": \"null\",\n  \"isLast\": true\n}",
                            summary = "답변 생성 완료"
                    )
            }, mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "OPENAI_SERVER_ERROR | INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = CheckAssistantInfoRespDto.class), mediaType = "application/json"))

    })
    Flux<com.develokit.maeum_ieum.dto.message.RespDto.CreateStreamMessageRespDto> createStreamMessage(
            @PathVariable(name = "elderlyId") Long elderlyId,
            @RequestBody @Valid CreateStreamMessageReqDto createStreamMessageReqDto,
            BindingResult bindingResult);
}
