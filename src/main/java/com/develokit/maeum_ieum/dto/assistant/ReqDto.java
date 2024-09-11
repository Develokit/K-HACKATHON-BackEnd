package com.develokit.maeum_ieum.dto.assistant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class ReqDto {
    @NoArgsConstructor
    @Getter
    @Schema(description = "노인 필수 규칙 자동 생성 요청을 위한 DTO")
    public static class AssistantMandatoryRuleReqDto{
        @Schema(description = "간단한 필수 규칙 내용: 100 자리 이내로 입력")
        @NotBlank(message = "필수 규칙 자동 생성을 위한 내용을 입력해야 합니다.")
        @Size(min = 1, max = 100, message = "100 자리 이내로 입력해야 합니다")
        private String content; //100자리
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "AI 어시스턴트 접속을 위한 DTO")
    public static class VerifyAccessCodeReqDto{
        @NotBlank
        @Size(min = 5, max = 5, message = "접속 코드 다섯 자리를 입력해주세요")
        @Schema(description = "접속 코드: 5자리 고정")
        private String accessCode;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "AI 어시스턴트 수정 요청을 위한 DTO")
    @Setter
    public static class AssistantModifyReqDto{

        @Nullable
        @Schema(description = "AI 이름: 최소 1자에서 최대 256자")
        @Size(min = 1, max = 256, message = "최소 1자에서 최대 256자까지 입력해야 합니다")
        private String name;

        @Nullable
        @Schema(description = "AI 규칙(필수): 최소 1자에서 최대 512자")
        @Size(min = 1, max = 512, message = "AI 규칙은 필수 설정입니다")
        private String mandatoryRule; //AI 필수 규칙 설정

        @Nullable
        @Schema(description = "AI 규칙(선택): 대화 주제")
        private String conversationTopic; //대화 주제 -> description
        @Nullable
        @Schema(description = "AI 규칙(선택): 응답 형식")
        private String responseType; //응답 형식
        @Nullable
        @Schema(description = "AI 규칙(선택): 성격")
        private String personality; //성격
        @Nullable
        @Schema(description = "AI 규칙(선택): 금기 주제")
        private String forbiddenTopic; //금기 주제
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "AI 어시스턴트 생성 요청을 위한 DTO")
    public static class CreateAssistantReqDto{

        @NotNull
        @Schema(description = "AI 이름: 최소 1자에서 최대 256자", required = true)
        @Size(min = 1, max = 256, message = "최소 1자에서 최대 256자까지 입력해야 합니다")
        private String name;

        @NotNull
        @Schema(description = "AI 규칙(필수): 최소 1자에서 최대 512자", required = true)
        @Size(min = 1, max = 512, message = "AI 규칙은 필수 설정입니다")
        private String mandatoryRule; //AI 필수 규칙 설정

        @Nullable
        @Schema(description = "AI 규칙(선택): 대화 주제")
        private String conversationTopic; //대화 주제 -> description
        @Nullable
        @Schema(description = "AI 규칙(선택): 응답 형식")
        private String responseType; //응답 형식
        @Nullable
        @Schema(description = "AI 규칙(선택): 성격")
        private String personality; //성격
        @Nullable
        @Schema(description = "AI 규칙(선택): 금기 주제")
        private String forbiddenTopic; //금기 주제
    }

}
