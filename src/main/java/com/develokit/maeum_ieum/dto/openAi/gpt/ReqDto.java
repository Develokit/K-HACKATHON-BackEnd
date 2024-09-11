package com.develokit.maeum_ieum.dto.openAi.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class ReqDto {

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CreateGptMessageReqDto{
        private String model;
        private List<MessageDto> messages = new ArrayList<>();
        //GPT 응답 생성 길이를 제어하는 토큰 크기
        private int maxTokens;

        public CreateGptMessageReqDto(String model, MessageDto system, MessageDto user, int maxTokens){
            this.model = model;
            this.messages.add(system);
            this.messages.add(user);
            this.maxTokens = maxTokens;
        }


        @NoArgsConstructor
        @Getter
        @AllArgsConstructor
        public static class MessageDto{
            private String content;
            private String role; //system, user만 사
        }
    }
}
