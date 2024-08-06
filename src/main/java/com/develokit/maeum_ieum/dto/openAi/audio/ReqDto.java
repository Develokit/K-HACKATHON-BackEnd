package com.develokit.maeum_ieum.dto.openAi.audio;

import com.develokit.maeum_ieum.domain.user.Gender;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

public class ReqDto {

    @NoArgsConstructor
    @Getter
    public static class CreateAudioReqDto{
        @NotNull(message = "content를 기재해야 합니다")
        private String content;

        @NotNull(message = "openAiAssistantId를 기재해야 합니다")
        private String openAiAssistantId;

        @NotNull(message = "threadId를 기재해야 합니다")
        private String threadId;

        @Pattern(regexp = "^(FEMALE|MALE)$", message = "성별은 FEMALE 또는 MALE이어야 합니다")
        private String gender;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Setter
    public static class AudioRequestDto{
        private String model;
        private String voice;
        private String input;

        public AudioRequestDto(String model, String voice) {
            this.model = model;
            this.voice = voice;
        }
    }
}
