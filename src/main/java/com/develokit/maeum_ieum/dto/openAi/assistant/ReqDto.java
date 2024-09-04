package com.develokit.maeum_ieum.dto.openAi.assistant;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ReqDto {


    @Getter
    @NoArgsConstructor
    public static class ModifyAssistantReqDto{
        @NotNull
        private String model;
        @Nullable
        @Size(max = 256)
        private String name;
        @Nullable
        @Size(max = 256000)
        private String instructions;
        @Nullable
        @Size(max = 512)
        private String description;

        @Builder
        public ModifyAssistantReqDto(String model, @Nullable String name, @Nullable String instructions, @Nullable String description) {
            this.model = model;
            this.name = name;
            this.instructions = instructions;
            this.description = description;
        }
    }


    @Getter
    @NoArgsConstructor
    public static class OpenAiCreateAssistantReqDto{

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
        private List<ToolDto> tools;

        //openAI에 보내는 DTO
        public OpenAiCreateAssistantReqDto(String model, @Nullable String name, @Nullable String instructions, @Nullable String description) {
            this.model = model;
            this.name = name;
            this.instructions = instructions;
            this.description = description;
        }

        @Builder
        public OpenAiCreateAssistantReqDto(@Nullable String name, @Nullable String instructions, @Nullable String description) {
            this.name = name;
            this.instructions = instructions;
            this.description = description;
        }
    }

    public static class ToolDto{
        private String type;
    }
}
