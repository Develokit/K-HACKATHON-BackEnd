package com.develokit.maeum_ieum.dto.openAi.run;

import com.develokit.maeum_ieum.dto.openAi.message.RespDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

public class ReqDto {

    @Getter
    @AllArgsConstructor
    @Builder
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CreateRunReqDto {
        @NotNull
        private String assistantId;
        private String instructions;
        private AdditionalMessageDto additionalMessages;
        private boolean stream; //true이면 런 이벤트에 대한 스트림을 생성

        public CreateRunReqDto (String assistantId, boolean stream){
            this.assistantId = assistantId;
            this.stream = stream;
        }

        @NoArgsConstructor
        @Getter
        private static class AdditionalMessageDto {
            private String role;
            private ContentDto content;

            @Getter
            @NoArgsConstructor
            public static class ContentDto{
                private String type;
                private TextDto text;

                @Getter
                @NoArgsConstructor
                public static class TextDto{
                    private String value;
                }
            }
        }
    }
}
