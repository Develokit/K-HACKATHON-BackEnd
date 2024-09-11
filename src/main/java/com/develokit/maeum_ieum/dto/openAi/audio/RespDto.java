package com.develokit.maeum_ieum.dto.openAi.audio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RespDto {

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Schema(description = "오디오 메시지 반환 DTO")
    public static class CreateAudioRespDto{
        @Schema(description = "오디오 바이트 파일")
        private byte[] answer;
        @Schema(description = "메시지")
        private String content;
        @Schema(description = "메시지 발행 시간")
        private String timeStamp;
    }
}
