package com.develokit.maeum_ieum.controller;

import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.dto.caregiver.ReqDto;
import com.develokit.maeum_ieum.dto.caregiver.RespDto;
import com.develokit.maeum_ieum.dto.openAi.assistant.RespDto.CreateAssistantRespDto;
import com.develokit.maeum_ieum.service.CaregiverService;
import com.develokit.maeum_ieum.util.ApiUtil;
import com.develokit.maeum_ieum.util.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import static com.develokit.maeum_ieum.dto.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.caregiver.ReqDto.*;
import static com.develokit.maeum_ieum.dto.caregiver.RespDto.*;
import static com.develokit.maeum_ieum.dto.elderly.ReqDto.*;
import static com.develokit.maeum_ieum.dto.elderly.RespDto.*;

@Tag(name = "요양사 API", description = "요양사가 호출하는 API 목록")

public interface CaregiverControllerDocs {
    @Operation(summary = "회원가입", description = "요양사 회원가입 기능: JoinReqDto 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공", content = @Content(schema = @Schema(implementation = JoinRespDto.class))),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = JoinRespDto.class)))
    })
    ResponseEntity<?> join(@Valid @RequestBody JoinReqDto joinReqDto, BindingResult bindingResult);


    @Operation(summary = "노인 사용자 생성", description = "노인 사용자 생성 기능: ElderlyCreateReqDto 사용")
    @ApiResponse(responseCode = "200", description = "노인 사용자 생성 성공", content = @Content(schema = @Schema(implementation = ElderlyCreateRespDto.class)))
    ResponseEntity<ApiResult<ElderlyCreateRespDto>> createElderly(@Valid @RequestBody ElderlyCreateReqDto elderlyCreateReqDto,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal LoginUser loginUser
    );
    @Operation(summary = "마이 페이지", description = "요양사의 마이 페이지 조회 기능: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "마이 페이지 조회 성공", content = @Content(schema = @Schema(implementation = MyInfoRespDto.class))),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = MyInfoRespDto.class)))
    })
    ResponseEntity<?> getCaregiverInfo(@AuthenticationPrincipal LoginUser loginUser);



    @Operation(summary = "어시스턴트 생성", description = "AI 어시스턴트 생성 기능: jwt 토큰, CreateAssistantReqDto 사용")
    @Parameter(description = "노인 사용자 아이디",in = ParameterIn.PATH)
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "AI 어시스턴트 생성 성공", content = @Content(schema = @Schema(implementation = CreateAssistantRespDto.class))),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = MyInfoRespDto.class)))
    })
    ResponseEntity<?> createAssistant(@RequestBody CreateAssistantReqDto createAssistantReqDto,
                                      @PathVariable(name = "elderlyId")Long elderlyId,
                                      BindingResult bindingResult,
                                      @AuthenticationPrincipal LoginUser loginUser);

    @Operation(summary = "메인 페이지", description = "메인 페이지 조회 기능: jwt 토큰 사용")
    @ApiResponse(responseCode = "200", description = "메인 페이지 조회 성공", content = @Content(schema = @Schema(implementation = CaregiverMainRespDto.class)))
    ResponseEntity<?> getCaregiverMainInfo(@AuthenticationPrincipal LoginUser loginUser);

}