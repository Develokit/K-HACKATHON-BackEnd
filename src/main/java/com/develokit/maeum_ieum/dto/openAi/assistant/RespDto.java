package com.develokit.maeum_ieum.dto.openAi.assistant;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.*;

import java.util.List;

public class RespDto {

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Builder
    @Schema(description = "AI 어시스턴트 생성 데이터 반환 DTO")
    public static class CreateAssistantRespDto{
        @Schema(description = "어시스턴트 아이디")
        private Long assistantId;
        @Schema(description = "어시스턴트 이름")
        private String name;
        @Schema(description = "어시스턴트 필수 규칙")
        private String mandatoryRule;

        @Schema(description = "AI 규칙(선택): 대화 주제")
        private String conversationTopic; //대화 주제 -> description

        @Schema(description = "AI 규칙(선택): 응답 형식")
        private String responseType; //응답 형식

        @Schema(description = "AI 규칙(선택): 성격")
        private String personality; //성격

        @Schema(description = "AI 규칙(선택): 금기 주제")
        private String forbiddenTopic; //금기 주제

        @Schema(description = "노인 사용자 접속 코드")
        private String accessCode;
        public CreateAssistantRespDto(Assistant assistant, String accessCode){
            this.assistantId = assistant.getId();
            this.name = assistant.getName();
            this.mandatoryRule = assistant.getMandatoryRule();
            this.accessCode = accessCode;
            this.conversationTopic = assistant.getConversationTopic()!=null?assistant.getConversationTopic():null;
            this.responseType = assistant.getResponseType()!=null?assistant.getResponseType():null;
            this.personality = assistant.getPersonality()!=null?assistant.getPersonality():null;
            this.forbiddenTopic = assistant.getForbiddenTopic()!=null?assistant.getForbiddenTopic():null;
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
