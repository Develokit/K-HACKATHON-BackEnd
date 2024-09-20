package com.develokit.maeum_ieum.dto.assistant;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.develokit.maeum_ieum.dto.openAi.gpt.RespDto.*;

public class RespDto {

    @NoArgsConstructor
    @Getter
    @Schema(description = "노인 필수 규칙 자동 생성 요청에 대한 응답 DTO")
    public static class AssistantMandatoryRuleRespDto{
        //TODO 테스트를 위해 콘솔 로그 놔둠 -> 성공 후 삭제할 것
        public AssistantMandatoryRuleRespDto(CreateGptMessageRespDto createGptMessageRespDto){
            CreateGptMessageRespDto.ChoiceDto choiceDto = createGptMessageRespDto.getChoices().get(0);
            int totalTokens = createGptMessageRespDto.getUsage().getTotalTokens();
            int completionTokens = createGptMessageRespDto.getUsage().getCompletionTokens();
            int promptTokens = createGptMessageRespDto.getUsage().getPromptTokens();

            System.out.println("promptTokens = " + promptTokens);
            System.out.println("completionTokens = " + completionTokens);
            System.out.println("totalTokens = " + totalTokens);

            String content = choiceDto.getMessage().getContent();
            String role = choiceDto.getMessage().getRole();

            System.out.println("role = " + role);
            System.out.println("content = " + content);

            this.mandatoryRule = content;
        }
        @Schema(description = "자동 생성된 필수 규칙")
        private String mandatoryRule;
    }

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

        public VerifyAccessCodeRespDto(Elderly elderly){
            this.assistantId = elderly.getAssistant().getId();
            this.elderlyId = elderly.getId();
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "AI 어시스턴트 조회 데이터 반환 DTO")
    public static class AssistantInfoRespDto{

        public AssistantInfoRespDto(Assistant assistant) {
            this.name = assistant.getName();
            this.mandatoryRule = assistant.getMandatoryRule();
            this.conversationTopic = assistant.getConversationTopic();
            this.responseType = assistant.getResponseType();
            this.personality = assistant.getPersonality();
            this.forbiddenTopic = assistant.getForbiddenTopic();
        }

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

    }
}
