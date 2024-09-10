package com.develokit.maeum_ieum.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RespDto {

    @NoArgsConstructor
    @Getter
    @Schema(description = "스트림 답변 DTO")
    public static class CreateStreamMessageRespDto{
        @Schema(description = "AI 어시스턴트 답변")
        private String answer;

        @Schema(description = "마지막 메시지를 나타내는 플래그: false면 마지막 메시지")
        private boolean isLast;

        @JsonProperty("isLast")
        public boolean isLast() {
            return isLast;
        }
        public CreateStreamMessageRespDto(String answer, boolean isLast){
            this.answer = answer;
            this.isLast = isLast;
        }
    }
}
