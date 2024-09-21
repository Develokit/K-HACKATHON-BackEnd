package com.develokit.maeum_ieum.controller.caregiver;

import com.develokit.maeum_ieum.config.jwt.RequireAuth;
import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.dto.caregiver.ReqDto;
import com.develokit.maeum_ieum.dto.caregiver.RespDto;
import com.develokit.maeum_ieum.dto.openAi.assistant.RespDto.CreateAssistantRespDto;
import com.develokit.maeum_ieum.service.AssistantService;
import com.develokit.maeum_ieum.service.CaregiverService;
import com.develokit.maeum_ieum.util.ApiUtil;
import com.develokit.maeum_ieum.util.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import static com.develokit.maeum_ieum.dto.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.assistant.RespDto.*;
import static com.develokit.maeum_ieum.dto.caregiver.ReqDto.*;
import static com.develokit.maeum_ieum.dto.caregiver.RespDto.*;
import static com.develokit.maeum_ieum.dto.elderly.ReqDto.*;
import static com.develokit.maeum_ieum.dto.elderly.RespDto.*;
import static com.develokit.maeum_ieum.dto.emergencyRequest.RespDto.*;
import static com.develokit.maeum_ieum.dto.report.RespDto.*;

@Tag(name = "요양사 API", description = "요양사가 호출하는 API 목록")

public interface CaregiverControllerDocs {

    @Operation(summary = "유저네임 중복 확인", description = "회원가입 시 유저네임 중복 여부 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복 확인 완료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaregiverDuplicatedRespDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "중복 있음 예시",
                                            value = "{\n  \"username\": \"userA3333\",\n  \"isDuplicated\": true\n}",
                                            summary = "유저네임 중복 시"
                                    ),
                                    @ExampleObject(
                                            name = "중복 없음 예시",
                                            value = "{\n  \"username\": \"newUser5555\",\n  \"isDuplicated\": false\n}",
                                            summary = "유저네임 사용 가능 시"
                                    )
                            }
                    )
            )
    })
    ResponseEntity<?> checkUsername(@PathVariable(name = "username")@Parameter(description = "확인할 유저네임") String username);
    @Operation(summary = "회원가입", description = "요양사 회원가입 기능: JoinReqDto 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공", content = @Content(schema = @Schema(implementation = JoinRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = JoinRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 아이디입니다", content = @Content(schema = @Schema(implementation = JoinRespDto.class), mediaType = "application/json"))
    })
    ResponseEntity<?> join(@Valid @ModelAttribute JoinReqDto joinReqDto, BindingResult bindingResult);


    @Operation(summary = "노인 사용자 생성", description = "노인 사용자 생성 기능: ElderlyCreateReqDto 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "노인 사용자 생성 성공", content = @Content(schema = @Schema(implementation = ElderlyCreateRespDto.class) , mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = ElderlyCreateRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authorization 헤더 재확인 바람", content = @Content(schema = @Schema(implementation = ElderlyCreateRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 서명", content = @Content(schema = @Schema(implementation = ElderlyCreateRespDto.class), mediaType = "application/json"))
    })
    ResponseEntity<?> createElderly(@Valid @ModelAttribute ElderlyCreateReqDto elderlyCreateReqDto,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal LoginUser loginUser
    );
    @Operation(summary = "마이 페이지 조회", description = "요양사의 마이 페이지 조회 기능: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "마이 페이지 조회 성공", content = @Content(schema = @Schema(implementation = MyInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = MyInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authorization 헤더 재확인 바람", content = @Content(schema = @Schema(implementation = MyInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 서명", content = @Content(schema = @Schema(implementation = MyInfoRespDto.class), mediaType = "application/json"))
    })
    ResponseEntity<?> getCaregiverInfo(@AuthenticationPrincipal LoginUser loginUser);



    @Operation(summary = "AI 어시스턴트 생성", description = "AI 어시스턴트 생성 기능: jwt 토큰, CreateAssistantReqDto 사용")
    @Parameter(description = "노인 사용자 아이디",in = ParameterIn.PATH)
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "AI 어시스턴트 생성 성공", content = @Content(schema = @Schema(implementation = CreateAssistantRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = CreateAssistantRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authorization 헤더 재확인 바람", content = @Content(schema = @Schema(implementation = CreateAssistantRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 서명", content = @Content(schema = @Schema(implementation = CreateAssistantRespDto.class), mediaType = "application/json"))
    })
    ResponseEntity<?> createAssistant(@Valid@RequestBody CreateAssistantReqDto createAssistantReqDto,
                                      @PathVariable(name = "elderlyId")Long elderlyId,
                                      BindingResult bindingResult,
                                      @AuthenticationPrincipal LoginUser loginUser);

    @Operation(summary = "메인 페이지 조회", description = "메인 페이지 조회 기능: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "메인 페이지 조회 성공", content = @Content(schema = @Schema(implementation = CaregiverMainRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = CaregiverMainRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authorization 헤더 재확인 바람", content = @Content(schema = @Schema(implementation = CaregiverMainRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 서명", content = @Content(schema = @Schema(implementation = CaregiverMainRespDto.class), mediaType = "application/json"))
    })ResponseEntity<?> getCaregiverMainInfo(
            @RequestParam(name = "cursor",required = false) @Parameter(description = "다음 데이터 조회를 위한 커서 값. 첫 요청 시 null 또는 비워둠. 다음 데이터 요청 시 이전 응답의 nextCursor를 사용") String cursor,
            @RequestParam(name = "limit", defaultValue = "10") @Parameter(description = "한 페이지에 표시할 항목 수. 기본값은 10") int limit,
            @AuthenticationPrincipal LoginUser loginUser);

    @Operation(summary = "마이 페이지 수정 (이미지 제외) ", description = "마이 페이지 수정 기능: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "마이 페이지 수정 성공", content = @Content(schema = @Schema(implementation = CaregiverModifyReqDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = CaregiverModifyReqDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authorization 헤더 재확인 바람", content = @Content(schema = @Schema(implementation = CaregiverModifyReqDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 서명", content = @Content(schema = @Schema(implementation = CaregiverModifyReqDto.class), mediaType = "application/json"))

    })
    ResponseEntity<?>modifyCaregiverInfo(@Valid@RequestBody CaregiverModifyReqDto caregiverModifyReqDto,
                                                BindingResult bindingResult,
                                                @AuthenticationPrincipal LoginUser loginUser);

    @Operation(summary = "마이 페이지 이미지 수정 ", description = "마이 페이지 이미지 수정 기능: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "마이 페이지 이미지 수정 성공", content = @Content(schema = @Schema(implementation = CaregiverImgModifyReqDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = CaregiverImgModifyReqDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authorization 헤더 재확인 바람", content = @Content(schema = @Schema(implementation = CaregiverImgModifyReqDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 서명", content = @Content(schema = @Schema(implementation = CaregiverImgModifyReqDto.class), mediaType = "application/json"))

    })
    ResponseEntity<?>modifyCaregiverImg(@RequestParam("img") MultipartFile img,
                                               @AuthenticationPrincipal LoginUser loginUser);

    @Operation(summary = "노인 기본 정보 수정 (이미지 제외) ", description = "노인 기본 정보 수정 기능: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "노인 기본 정보 수정 성공", content = @Content(schema = @Schema(implementation = ElderlyModifyRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = ElderlyModifyRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authorization 헤더 재확인 바람", content = @Content(schema = @Schema(implementation = ElderlyModifyRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 서명", content = @Content(schema = @Schema(implementation = ElderlyModifyRespDto.class), mediaType = "application/json"))

    })
    ResponseEntity<?>modifyElderlyInfo(@Valid@RequestBody ElderlyModifyReqDto elderlyModifyReqDto, BindingResult bindingResult, @PathVariable(value = "elderlyId")Long elderlyId
            , @AuthenticationPrincipal LoginUser loginUser);

    @Operation(summary = "노인 이미지 수정 ", description = "노인 이미지 수정 기능: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "노인 이미지 수정 성공", content = @Content(schema = @Schema(implementation = ElderlyImgModifyRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = ElderlyImgModifyRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authorization 헤더 재확인 바람", content = @Content(schema = @Schema(implementation = ElderlyImgModifyRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 서명", content = @Content(schema = @Schema(implementation = ElderlyImgModifyRespDto.class), mediaType = "application/json"))

    })
    ResponseEntity<?> modifyElderlyImg(@RequestParam(value = "img", required = false) MultipartFile img,
                                       @PathVariable(name = "elderlyId")Long elderlyId,
                                       @AuthenticationPrincipal LoginUser loginUser);


    @Operation(summary = "AI 어시스턴트 수정 ", description = "어시스턴트 수정 기능: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "어시스턴트 수정 성공", content = @Content(schema = @Schema(implementation = AssistantModifyRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = AssistantModifyRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authorization 헤더 재확인 바람", content = @Content(schema = @Schema(implementation = AssistantModifyRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 서명", content = @Content(schema = @Schema(implementation = AssistantModifyRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "OPENAI_SERVER_ERROR | INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = AssistantModifyRespDto.class), mediaType = "application/json"))

    })
    @RequireAuth
    @PatchMapping(value = "/elderlys/{elderlyId}/assistants/{assistantId}")
    ResponseEntity<?> modifyAssistantInfo(@Valid@RequestBody AssistantModifyReqDto assistantModifyReqDto,
                                                 @PathVariable(name = "elderlyId")Long elderlyId,
                                                 @PathVariable(name = "assistantId")Long assistantId,
                                                 BindingResult bindingResult,
                                                 @AuthenticationPrincipal LoginUser loginUser);


    @Operation(summary = "AI 어시스턴트 삭제 ", description = "어시스턴트 삭제 기능: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "어시스턴트 삭제 성공", content = @Content(schema = @Schema(implementation = AssistantDeleteRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = AssistantDeleteRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authorization 헤더 재확인 바람", content = @Content(schema = @Schema(implementation = AssistantDeleteRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 서명", content = @Content(schema = @Schema(implementation = AssistantDeleteRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "OPENAI_SERVER_ERROR | INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = AssistantDeleteRespDto.class), mediaType = "application/json"))

    })
    ResponseEntity<?> deleteAssistant(@PathVariable(name = "elderlyId")Long elderlyId,
                                             @PathVariable(name = "assistantId")Long assistantId,
                                             @AuthenticationPrincipal LoginUser loginUser );


    @Operation(summary = "노인 기본 정보 조회 ", description = "노인 기본 정보 페이지 진입 시 요청: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = ElderlyInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = ElderlyInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authorization 헤더 재확인 바람", content = @Content(schema = @Schema(implementation = ElderlyInfoRespDto.class), mediaType = "application/json")),
    })
    ResponseEntity<?> getElderlyInfo(@PathVariable(name = "elderlyId")Long elderlyId, @AuthenticationPrincipal LoginUser loginUser);

    @Operation(summary = "AI 어시스턴트 필수 규칙 자동 생성 ", description = "AI 어시스턴트 필수 규칙 자동 생성 요청: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = AssistantMandatoryRuleReqDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = AssistantMandatoryRuleReqDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "GPT 메시지 생성 과정에서 에러 발생", content = @Content(schema = @Schema(implementation = AssistantMandatoryRuleReqDto.class), mediaType = "application/json")),
    })
    Mono<ResponseEntity<?>> createAutoMandatoryRule(@PathVariable(name = "elderlyId")Long elderlyId,
                                                    @Valid @RequestBody AssistantMandatoryRuleReqDto assistantMandatoryRuleReqDto,
                                                    BindingResult bindingResult,
                                                    @AuthenticationPrincipal LoginUser loginUser);
    @Operation(summary = "알림 내역 페이지 조회 ", description = "관리하는 노인 사용자가 발행한 알림 내역 조회: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = EmergencyRequestListRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = EmergencyRequestListRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = EmergencyRequestListRespDto.class), mediaType = "application/json")),
    })
    ResponseEntity<?> getEmergencyRequestList(@RequestParam(name = "page", defaultValue = "0")int page,
                                              @RequestParam(name = "size", defaultValue = "10")int size,
                                              @AuthenticationPrincipal LoginUser loginUser);

    @Operation(summary = "주간 보고서 생성 날짜 수정 ", description = "주간 보고서 생성 날짜 수정: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = ElderlyReportDayModifyRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = ElderlyReportDayModifyRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ElderlyReportDayModifyRespDto.class), mediaType = "application/json")),
    })
    ResponseEntity<?> modifyElderlyReportDay(@PathVariable(name = "elderlyId")Long elderlyId,
                                             @RequestBody@Valid ElderlyReportDayModifyReqDto elderlyReportDayModifyReqDto,
                                             @AuthenticationPrincipal LoginUser loginUser );


    @Operation(summary = "AI 어시스턴트 정보 조회 ", description = "AI 어시스턴트 정보 페이지 진입 시 요청: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = AssistantInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = AssistantInfoRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authorization 헤더 재확인 바람", content = @Content(schema = @Schema(implementation = AssistantInfoRespDto.class), mediaType = "application/json")),
    })
    ResponseEntity<?> getAssistantInfo(@PathVariable(name = "elderlyId")Long elderlyId,
                                                 @PathVariable(name = "assistantId")Long assistantId,
                                                 @AuthenticationPrincipal LoginUser loginUser);

    @Operation(summary = "노인 주간 보고서 리스트 조회 ", description = "노인 기본 정보 페이지에서 생성된 주간 보고서 조회 시 요청: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = WeeklyReportListRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = WeeklyReportListRespDto.class), mediaType = "application/json")),
    })
    ResponseEntity<?> getElderlyWeeklyReports(@PathVariable(name = "elderlyId")Long elderlyId,
                                               @RequestParam(name = "cursor",required = false) @Parameter(description = "다음 데이터 조회를 위한 커서 값. 첫 요청 시 null. 다음 데이터 요청 시 이전 응답의 nextCursor를 사용") Long cursor,
                                               @RequestParam(name = "limit", defaultValue = "10") @Parameter(description = "한 페이지에 표시할 항목 수. 기본값은 10") int limit,
                                               @AuthenticationPrincipal LoginUser loginUser);


    @Operation(summary = "노인 월간 보고서 리스트 조회 ", description = "노인 기본 정보 페이지에서 생성된 월간 보고서 조회 시 요청: jwt 토큰 사용")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = MonthlyReportListRespDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "토큰 기간 만료", content = @Content(schema = @Schema(implementation = MonthlyReportListRespDto.class), mediaType = "application/json")),
    })
    public ResponseEntity<?> getElderlyMonthlyReports(@PathVariable(name = "elderlyId")Long elderlyId,
                                                      @RequestParam(name = "cursor",required = false) @Parameter(description = "다음 데이터 조회를 위한 커서 값. 첫 요청 시 null 또는 비워둠. 다음 데이터 요청 시 이전 응답의 nextCursor를 사용") Long cursor,
                                                      @RequestParam(name = "limit", defaultValue = "10") @Parameter(description = "한 페이지에 표시할 항목 수. 기본값은 10") int limit,
                                                      @AuthenticationPrincipal LoginUser loginUser);
}