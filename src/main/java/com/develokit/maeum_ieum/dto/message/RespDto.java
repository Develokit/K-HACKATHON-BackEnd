package com.develokit.maeum_ieum.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RespDto {

    @NoArgsConstructor
    @Getter
    @Schema(description = "스트림 답변 DTO")
    public static class CreateStreamMessageRespDto{
        @Schema(description = "AI 어시스턴트 답변")
        private String answer;

        @Schema(description = "마지막 메시지를 나타내는 플래그: false면 마지막 메시지")
        private boolean isLast;

        @Schema(description = "스트림 메시지 발행 시간: 마지막 응답 반환 시에만 기재")
        private String timeStamp;

        @JsonProperty("isLast")
        public boolean isLast() {
            return isLast;
        }
        public CreateStreamMessageRespDto(String answer, boolean isLast, String timeStamp){
            this.answer = answer;
            this.isLast = isLast;
            this.timeStamp =timeStamp;
        }
    }
}
