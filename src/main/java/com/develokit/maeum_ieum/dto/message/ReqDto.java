package com.develokit.maeum_ieum.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReqDto {
    @Getter
    @NoArgsConstructor
    public static class CreateStreamMessageReqDto{
        @NotNull(message = "content를 기재해야 합니다")
        private String content;

        @NotNull(message = "openAiAssistantId를 기재해야 합니다")
        private String openAiAssistantId;

        @NotNull(message = "threadId를 기재해야 합니다")
        private String threadId;

    }

}
