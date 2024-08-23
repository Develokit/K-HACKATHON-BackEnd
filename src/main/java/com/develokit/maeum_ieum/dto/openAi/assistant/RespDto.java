package com.develokit.maeum_ieum.dto.openAi.assistant;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

public class RespDto {

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Builder
    @Schema(description = "AI 어시스턴트 생성 데이터 반환 DTO")
    public static class CreateAssistantRespDto{
        @Schema(description = "openAI 어시스턴트 아이디")
        private String openAiAssistantId;
        @Schema(description = "어시스턴트 아이디")
        private Long assistantId;
        @Schema(description = "어시스턴트 이름")
        private String name;
        @Schema(description = "어시스턴트 필수 규칙")
        private String description;
        @Schema(description = "openAI 어시스턴트 전체 규칙")
        private String instructions;
        @Schema(description = "노인 사용자 접속 코드")
        private String accessCode;
        public CreateAssistantRespDto(Assistant assistant, String instructions){
            this.assistantId = assistant.getId();
            this.openAiAssistantId = assistant.getOpenAiAssistantId();
            this.name = assistant.getName();
            this.description = assistant.getRule();
            this.instructions = instructions;
            this.accessCode = assistant.getAccessCode();

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
