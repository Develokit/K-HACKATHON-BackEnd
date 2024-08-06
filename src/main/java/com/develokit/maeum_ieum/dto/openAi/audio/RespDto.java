package com.develokit.maeum_ieum.dto.openAi.audio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RespDto {

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    public static class CreateAudioRespDto{
        private byte[] answer;
    }
}
