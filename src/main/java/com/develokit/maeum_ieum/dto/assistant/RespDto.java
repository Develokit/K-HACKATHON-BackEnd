package com.develokit.maeum_ieum.dto.assistant;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RespDto {

    @Getter
    @NoArgsConstructor
    @Schema(description = "어시스턴트 삭제 후 반환되는 DTO")
    public static class AssistantDeleteRespDto{
        public AssistantDeleteRespDto(Elderly elderly) {
            this.elderlyName = elderly.getName();
            this.deleted = true;
        }

        private String elderlyName;
        private boolean deleted;
    }
    @Getter
    @NoArgsConstructor
    @Schema(description = "어시스턴트 수정 후 반환되는 DTO")
    public static class AssistantModifyRespDto{
        public AssistantModifyRespDto(Assistant assistant) {
            this.name = assistant.getName();
            this.mandatoryRule = assistant.getMandatoryRule();
            this.conversationTopic = assistant.getConversationTopic();
            this.responseType = assistant.getResponseType();
            this.personality = assistant.getPersonality();
            this.forbiddenTopic = assistant.getForbiddenTopic();
        }

        @Schema(description = "AI 이름: 최소 1자에서 최대 256자")
        private String name;

        @Schema(description = "AI 규칙(필수): 최소 1자에서 최대 512자")
        private String mandatoryRule; //AI 필수 규칙 설정

        @Schema(description = "AI 규칙(선택): 대화 주제")
        private String conversationTopic; //대화 주제 -> description

        @Schema(description = "AI 규칙(선택): 응답 형식")
        private String responseType; //응답 형식

        @Schema(description = "AI 규칙(선택): 성격")
        private String personality; //성격

        @Schema(description = "AI 규칙(선택): 금기 주제")
        private String forbiddenTopic; //금기 주제
    }

    @NoArgsConstructor
    @Getter
    public static class VerifyAccessCodeRespDto{
        private Long assistantId;
        private Long elderlyId;

        public VerifyAccessCodeRespDto(Assistant assistant){
            this.assistantId = assistant.getId();
            this.elderlyId = assistant.getElderly().getId();
        }
    }
}
