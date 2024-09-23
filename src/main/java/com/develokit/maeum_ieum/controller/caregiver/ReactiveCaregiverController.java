package com.develokit.maeum_ieum.controller.caregiver;

import com.develokit.maeum_ieum.dto.assistant.ReqDto;
import com.develokit.maeum_ieum.dto.assistant.RespDto;
import com.develokit.maeum_ieum.service.CaregiverService;
import com.develokit.maeum_ieum.util.ApiUtil;
import com.develokit.maeum_ieum.util.api.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.develokit.maeum_ieum.dto.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.assistant.RespDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reactive/caregivers")
public class ReactiveCaregiverController {
    private final CaregiverService caregiverService;
    private final Logger log = LoggerFactory.getLogger(ReactiveCaregiverController.class);

    @PostMapping("/elderlys/{elderlyId}/assistants/rules/autocomplete")
    public Mono<ResponseEntity<ApiResult<AssistantMandatoryRuleRespDto>>> createAutoMandatoryRule(
            @PathVariable(name = "elderlyId") Long elderlyId,
            @Valid @RequestBody AssistantMandatoryRuleReqDto assistantMandatoryRuleReqDto) {

        log.debug("디버그 : 노인 필수 규칙 자동 생성을 위한 리액티브 컨트롤러 진입 완료");


        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    log.debug("Security Context: {}", securityContext.getAuthentication());
                    return caregiverService.createAutoMandatoryRule(assistantMandatoryRuleReqDto)
                            .map(assistantMandatoryRuleRespDto -> {
                                log.debug("노인 필수 규칙 자동 생성 완료");
                                return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(ApiUtil.success(assistantMandatoryRuleRespDto));
                            });
                });
    }

}
