package com.develokit.maeum_ieum.dto.openAi.audio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReqDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AudioRequestDto{
        private String model;
        private String voice;
        private String input;

    }
}
