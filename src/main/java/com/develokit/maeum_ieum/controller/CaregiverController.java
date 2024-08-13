package com.develokit.maeum_ieum.controller;

import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.dto.assistant.ReqDto.CreateAssistantReqDto;
import com.develokit.maeum_ieum.dto.caregiver.ReqDto;
import com.develokit.maeum_ieum.dto.elderly.ReqDto.ElderlyCreateReqDto;
import com.develokit.maeum_ieum.dto.elderly.RespDto;
import com.develokit.maeum_ieum.service.CaregiverService;
import com.develokit.maeum_ieum.service.ElderlyService;
import com.develokit.maeum_ieum.util.ApiUtil;
import com.develokit.maeum_ieum.util.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.beans.BeanInfo;

import static com.develokit.maeum_ieum.dto.caregiver.ReqDto.*;
import static com.develokit.maeum_ieum.dto.elderly.RespDto.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/caregivers")
public class CaregiverController implements CaregiverControllerDocs {

    private final CaregiverService caregiverService;
    private final ElderlyService elderlyService;

    @PostMapping
    public ResponseEntity<?> join(@Valid @RequestBody JoinReqDto joinReqDto, BindingResult bindingResult){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.join(joinReqDto)), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getCaregiverMainInfo(@AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.getCaregiverMainInfo(loginUser.getUsername())),HttpStatus.OK);
    }

    @PostMapping("/elderlys")
    public ResponseEntity<ApiResult<ElderlyCreateRespDto>> createElderly(@Valid @RequestBody ElderlyCreateReqDto elderlyCreateReqDto,
                                                                         BindingResult bindingResult,
                                                                         @AuthenticationPrincipal LoginUser loginUser) {

        return new ResponseEntity<>(ApiUtil.success(elderlyService.createElderly(elderlyCreateReqDto, loginUser.getCaregiver().getUsername())), HttpStatus.CREATED);
    }
    @PostMapping("/elderlys/{elderlyId}/assistants") //AI Assistant 생성
    public ResponseEntity<?> createAssistant(@RequestBody CreateAssistantReqDto createAssistantReqDto,
                                             @PathVariable(name = "elderlyId")Long elderlyId,
                                             BindingResult bindingResult,
                                             @AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.attachAssistantToElderly(createAssistantReqDto, elderlyId, loginUser.getCaregiver().getUsername())),HttpStatus.CREATED);
    }


    @GetMapping("/mypage") //내 정보
    public ResponseEntity<?> getCaregiverInfo(@AuthenticationPrincipal LoginUser loginUser){
        return new ResponseEntity<>(ApiUtil.success(caregiverService.caregiverInfo(loginUser.getUsername())),HttpStatus.OK);
    }
}
