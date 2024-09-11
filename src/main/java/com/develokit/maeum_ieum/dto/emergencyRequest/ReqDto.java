package com.develokit.maeum_ieum.dto.emergencyRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReqDto {
    @NoArgsConstructor
    @Getter
    @Schema(description = "노인 긴급 알림 생성을 위한 DTO")
    public static class EmergencyRequestCreateReqDto{
        @Schema(description = "긴급 알림 유형")
        @Pattern(regexp = "^CAREGIVER_NOTIFY$", message = "현재 긴급 알림 유형으로 CAREGIVER_NOTIFY(요양사 알림)만 가능")
        private String emergencyType;

    }
}
