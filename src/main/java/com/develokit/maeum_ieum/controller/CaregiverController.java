package com.develokit.maeum_ieum.controller;

import com.develokit.maeum_ieum.config.jwt.RequireAuth;
import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.dto.assistant.ReqDto.CreateAssistantReqDto;
import com.develokit.maeum_ieum.dto.caregiver.ReqDto;
import com.develokit.maeum_ieum.dto.elderly.ReqDto.ElderlyCreateReqDto;
import com.develokit.maeum_ieum.dto.elderly.RespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.service.AssistantService;
import com.develokit.maeum_ieum.service.CaregiverService;
import com.develokit.maeum_ieum.service.ElderlyService;
import com.develokit.maeum_ieum.util.ApiUtil;
import com.develokit.maeum_ieum.util.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.print.attribute.standard.Media;
import java.beans.BeanInfo;
import java.net.MalformedURLException;

import static com.develokit.maeum_ieum.dto.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.caregiver.ReqDto.*;
import static com.develokit.maeum_ieum.dto.elderly.ReqDto.*;
import static com.develokit.maeum_ieum.dto.elderly.RespDto.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/caregivers")
public class CaregiverController implements CaregiverControllerDocs {

    private final CaregiverService caregiverService;
    private final ElderlyService elderlyService;
    private final AssistantService assistantService;
    private final Logger log = LoggerFactory.getLogger(CaregiverController.class);

    @GetMapping("/check-username/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable(name = "username") String username) {
        return new ResponseEntity<>(ApiUtil.success(caregiverService.validateDuplicatedCaregiver(username)),HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> join(@Valid @ModelAttribute JoinReqDto joinReqDto, BindingResult bindingResult){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.join(joinReqDto)), HttpStatus.CREATED);
    }

    @RequireAuth
    @GetMapping
    public ResponseEntity<?> getCaregiverMainInfo(
            @RequestParam(name = "cursor",required = false) @Parameter(description = "다음 데이터 조회를 위한 커서 값. 첫 요청 시 null 또는 비워둠. 다음 데이터 요청 시 이전 응답의 nextCursor를 사용") String cursor,
            @RequestParam(name = "limit", defaultValue = "10") @Parameter(description = "한 페이지에 표시할 항목 수. 기본값은 10") int limit,
            @AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.getCaregiverMainInfo(loginUser.getUsername(), cursor, limit)),HttpStatus.OK);
    }

    //노인 사용자 생성
    @RequireAuth
    @PostMapping(value = "/elderlys", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createElderly(@Valid @ModelAttribute ElderlyCreateReqDto elderlyCreateReqDto,
                                                                         BindingResult bindingResult,
                                                                         @AuthenticationPrincipal LoginUser loginUser) {
        return new ResponseEntity<>(ApiUtil.success(elderlyService.createElderly(elderlyCreateReqDto, loginUser.getCaregiver().getUsername())), HttpStatus.CREATED);
    }
    @RequireAuth
    @PostMapping("/elderlys/{elderlyId}/assistants") //AI Assistant 생성
    public ResponseEntity<?> createAssistant(@Valid@RequestBody CreateAssistantReqDto createAssistantReqDto,
                                             @PathVariable(name = "elderlyId")Long elderlyId,
                                             BindingResult bindingResult,
                                             @AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.attachAssistantToElderly(createAssistantReqDto, elderlyId, loginUser.getCaregiver().getUsername())),HttpStatus.CREATED);
    }

    @RequireAuth
    @GetMapping("/mypage") //내 정보
    public ResponseEntity<?> getCaregiverInfo(@AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.caregiverInfo(loginUser.getCaregiver().getUsername())),HttpStatus.OK);
    }

    @RequireAuth
    @PatchMapping("/mypage") //마이페이지 수정(이미지 제외)
    public ResponseEntity<?>modifyCaregiverInfo(@Valid@RequestBody CaregiverModifyReqDto caregiverModifyReqDto,
                                                BindingResult bindingResult,
                                                @AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.modifyCaregiverInfo(loginUser.getCaregiver().getUsername(), caregiverModifyReqDto)), HttpStatus.OK);
    }

    //마이페이지 이미지 수정
    @RequireAuth
    @PatchMapping(value = "/mypage/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?>modifyCaregiverImg(@RequestParam(value = "img", required = false) MultipartFile img,
                                               @AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.modifyCaregiverImg(loginUser.getCaregiver().getUsername(), img)), HttpStatus.OK);
    }

    //노인 기본 정보 수정(이미지 제외)
    @RequireAuth
    @PatchMapping(value = "/elderlys/{elderlyId}")
    public ResponseEntity<?>modifyElderlyInfo(@Valid@RequestBody ElderlyModifyReqDto elderlyModifyReqDto, BindingResult bindingResult, @PathVariable(value = "elderlyId")Long elderlyId
                                              ,@AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(elderlyService.modifyElderlyInfo(elderlyModifyReqDto, elderlyId, loginUser.getCaregiver().getUsername())), HttpStatus.OK);
    }

    //노인 기본 정보 이미지 수정
    @RequireAuth
    @PatchMapping(value = "/elderlys/{elderlyId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> modifyElderlyImg(@RequestParam(value = "img", required = false) MultipartFile img,
                                               @PathVariable(name = "elderlyId")Long elderlyId,
                                               @AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(elderlyService.modifyElderlyImg(img, elderlyId)), HttpStatus.OK);
    }

    //어시스턴트 수정
    @RequireAuth
    @PatchMapping(value = "/elderlys/{elderlyId}/assistants/{assistantId}")
    public ResponseEntity<?> modifyAssistantInfo(@Valid@RequestBody AssistantModifyReqDto assistantModifyReqDto,
                                                 @PathVariable(name = "elderlyId")Long elderlyId,
                                                 @PathVariable(name = "assistantId")Long assistantId,
                                                 BindingResult bindingResult,
                                                 @AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(assistantService.modifyAssistantInfo(assistantModifyReqDto, elderlyId, assistantId)), HttpStatus.OK);
    }


    // 어시스턴트 삭제
    @RequireAuth
    @DeleteMapping(value = "/elderlys/{elderlyId}/assistants/{assistantId}")
    public ResponseEntity<?> deleteAssistant(@PathVariable(name = "elderlyId")Long elderlyId,
                                             @PathVariable(name = "assistantId")Long assistantId,
                                             @AuthenticationPrincipal LoginUser loginUser ){
        return new ResponseEntity<>(ApiUtil.success(assistantService.deleteAssistant(assistantId, elderlyId, loginUser.getUsername())), HttpStatus.OK);
    }

    //노인 기본 정보 조회
    @RequireAuth
    @GetMapping("/elderlys/{elderlyId}")
    public ResponseEntity<?> getElderlyInfo(@PathVariable(name = "elderlyId")Long elderlyId, @AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.getElderlyInfo(elderlyId, loginUser.getCaregiver().getUsername())), HttpStatus.OK);
    }

    //노인 필수 규칙 자동 완성
    @RequireAuth
    @PostMapping("/elderlys/{elderlyId}/assistants/rules/autocomplete")
    public Mono<ResponseEntity<?>> createAutoMandatoryRule(@PathVariable(name = "elderlyId")Long elderlyId,
                                                           @Valid @RequestBody AssistantMandatoryRuleReqDto assistantMandatoryRuleReqDto,
                                                           BindingResult bindingResult,
                                                           @AuthenticationPrincipal LoginUser loginUser){
        return caregiverService.createAutoMandatoryRule(assistantMandatoryRuleReqDto)
                .map(assistantMandatoryRuleRespDto -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(ApiUtil.success(assistantMandatoryRuleRespDto))
                );
    }


}
