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
    @AllArgsConstructor
    @Builder
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
        private List<ToolDto> tools;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateAssistantReqDto{
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
        private List<ToolDto> tools;

    }

    public static class ToolDto{
        private String type;
    }
}
