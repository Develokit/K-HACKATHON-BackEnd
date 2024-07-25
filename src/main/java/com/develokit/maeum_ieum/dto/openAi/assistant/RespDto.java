package com.develokit.maeum_ieum.dto.openAi.assistant;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RespDto {

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
