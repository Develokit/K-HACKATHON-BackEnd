package com.develokit.maeum_ieum.dto.assistant;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RespDto {

    @NoArgsConstructor
    @Getter
    public static class VerifyAccessCodeRespDto{
        private Long assistantId;
        private String openAiAssistantId;
        private Long elderlyId;

        public VerifyAccessCodeRespDto(Assistant assistant){
            this.assistantId = assistant.getId();
            this.openAiAssistantId = assistant.getOpenAiAssistantId();
            this.elderlyId = assistant.getElderly().getId();
        }
    }
}
