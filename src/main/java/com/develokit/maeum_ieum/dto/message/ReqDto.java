package com.develokit.maeum_ieum.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReqDto {
    @Getter
    @NoArgsConstructor
    @Schema(description = "스트림 메시지 생성 요청을 위한 DTO")
    public static class CreateStreamMessageReqDto{
        @NotNull(message = "content를 기재해야 합니다")
        @Schema(description = "메시지 내용")
        private String content;

        @NotNull(message = "openAiAssistantId를 기재해야 합니다")
        private String openAiAssistantId;

        @NotNull(message = "threadId를 기재해야 합니다")
        private String threadId;

    }

}
