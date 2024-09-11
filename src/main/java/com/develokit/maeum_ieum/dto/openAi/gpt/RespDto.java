package com.develokit.maeum_ieum.dto.openAi.gpt;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RespDto {

    @NoArgsConstructor
    @Getter
    public static class CreateGptMessageRespDto{
        private List<ChoiceDto> choices;
        private UsageDto usage;

        @NoArgsConstructor
        @Getter
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class UsageDto{
            private int completionTokens;
            private int promptTokens;
            private int totalTokens;
        }

        @NoArgsConstructor
        @Getter
        public static class ChoiceDto{
            private int index;
            private MessageDto message;

            @NoArgsConstructor
            @Getter
            public static class MessageDto{
                private String content;
                private String role; //항상 assistant로 반환됨
            }
        }
    }
}
