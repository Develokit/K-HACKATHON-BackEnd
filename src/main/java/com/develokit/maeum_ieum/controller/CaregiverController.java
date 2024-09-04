package com.develokit.maeum_ieum.controller;

import com.develokit.maeum_ieum.config.jwt.RequireAuth;
import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.dto.assistant.ReqDto.CreateAssistantReqDto;
import com.develokit.maeum_ieum.dto.caregiver.ReqDto;
import com.develokit.maeum_ieum.dto.elderly.ReqDto.ElderlyCreateReqDto;
import com.develokit.maeum_ieum.dto.elderly.RespDto;
import com.develokit.maeum_ieum.service.AssistantService;
import com.develokit.maeum_ieum.service.CaregiverService;
import com.develokit.maeum_ieum.service.ElderlyService;
import com.develokit.maeum_ieum.util.ApiUtil;
import com.develokit.maeum_ieum.util.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> join(@Valid @ModelAttribute JoinReqDto joinReqDto, BindingResult bindingResult){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.join(joinReqDto)), HttpStatus.CREATED);
    }

    @RequireAuth
    @GetMapping
    public ResponseEntity<?> getCaregiverMainInfo(@AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.getCaregiverMainInfo(loginUser.getUsername())),HttpStatus.OK);
    }

    @RequireAuth
    @PostMapping("/elderlys")
    public ResponseEntity<ApiResult<ElderlyCreateRespDto>> createElderly(@Valid @RequestBody ElderlyCreateReqDto elderlyCreateReqDto,
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
    public ResponseEntity<?>modifyElderlyInfo(@Valid@RequestBody ElderlyModifyReqDto elderlyModifyReqDto, @PathVariable(value = "elderlyId")Long elderlyId,
                                              BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(elderlyService.modifyElderlyInfo(elderlyModifyReqDto, elderlyId)), HttpStatus.OK);
    }

    //노인 기본 정보 이미지 수정
    @RequireAuth
    @PatchMapping(value = "/elderlys/{elderlyId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> modifyElderlyImg(@RequestParam(value = "img", required = false) MultipartFile img,
                                               @PathVariable(name = "elderlyId")Long elderlyId,
                                               BindingResult bindingResult,
                                               @AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(elderlyService.modifyElderlyImg(img, elderlyId)), HttpStatus.OK);
    }

    //TODO 어시스턴트 수정
    @RequireAuth
    @PatchMapping(value = "/elderlys/{elderlyId}/assistants/{assistantId}")
    public ResponseEntity<?> modifyAssistantInfo(@Valid@RequestBody AssistantModifyReqDto assistantModifyReqDto,
                                                 @PathVariable(name = "elderlyId")Long elderlyId,
                                                 @PathVariable(name = "assistantId")Long assistantId,
                                                 BindingResult bindingResult,
                                                 @AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(assistantService.modifyAssistantInfo(assistantModifyReqDto, elderlyId, assistantId)), HttpStatus.OK);
    }


    //TODO 어시스턴트 삭제
    @RequireAuth
    @DeleteMapping(value = "/elderlys/{elderlyId}/assistants/{assistantId}")
    public ResponseEntity<?> deleteAssistant(@PathVariable(name = "elderlyId")Long elderlyId,
                                             @PathVariable(name = "assistantId")Long assistantId,
                                             @AuthenticationPrincipal LoginUser loginUser ){
        return new ResponseEntity<>(ApiUtil.success(assistantService.deleteAssistant(assistantId, elderlyId, loginUser.getUsername())), HttpStatus.OK);
    }
}
