package com.develokit.maeum_ieum.dto.message;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class RespDto {

    @NoArgsConstructor
    @Getter
    public static class CreateStreamMessageRespDto{
        private String answer;
        public CreateStreamMessageRespDto(String answer){
            this.answer = answer;
        }
    }
}
