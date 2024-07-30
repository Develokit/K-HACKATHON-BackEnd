package com.develokit.maeum_ieum.dto.assistant;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ReqDto {

    @Getter
    @NoArgsConstructor
    public static class CreateAssistantReqDto{

        private String model;
        @Nullable
        @Size(max = 256)
        private String name;
        @Nullable
        @Size(max = 256000)
        private String instructions;
        @Nullable
        @Size(min = 1, max = 512, message = "AI 규칙은 필수 설정입니다")
        private String description; //AI 필수 규칙 설정
        private List<com.develokit.maeum_ieum.dto.openAi.assistant.ReqDto.ToolDto> tools;
        //==========
        private String conversationTopic; //대화 주제 -> description
        private String responseType; //응답 형식
        private String personality; //성격
        private String forbiddenTopic; //금기 주제

        public void setInstructions(String instructions){
            this.instructions = instructions;
        }
    }

    public static class ToolDto{
        private String type;
    }
}
