package com.develokit.maeum_ieum.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

public class ReqDto {

    @NoArgsConstructor
    @Getter
    @Schema(description = "보고서 메모 생성 요청 DTO")
    public static class ReportMemoCreateReqDto{

        @Length(max = 512, message = "최대 512자까지 입력해야 합니다")
        @Schema(description = "작성할 메모")
        private String memo;
    }
}
