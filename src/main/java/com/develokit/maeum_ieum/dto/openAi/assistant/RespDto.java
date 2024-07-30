package com.develokit.maeum_ieum.dto.openAi.assistant;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import lombok.*;

import java.util.List;

public class RespDto {

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Builder
    public static class CreateAssistantRespDto{
        private String assistantId;
        private String name;
        private String description;
        private String instructions;
        public CreateAssistantRespDto(Assistant assistant, String instructions){
            this.assistantId = assistant.getOpenAiAssistantId();
            this.name = assistant.getName();
            this.description = assistant.getRule();
            this.instructions = instructions;
        }
    }


    @NoArgsConstructor
    @Getter
    public static class ListAssistantRespDto{
        private String object;
        private List<AssistantRespDto> assistantRespDtoList;
    }

    @NoArgsConstructor
    @Getter
    public static class AssistantRespDto{
        private String id;
        private String object;
        private Long createdAt;
        private String name;
        private String description;
        private String model;
        private String instructions;
        private List<ToolDto> tools;
        private Object metadata;
    }


    @Data
    public static class ToolDto {
        private String type;
    }
}
