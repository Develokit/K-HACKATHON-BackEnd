package com.develokit.maeum_ieum.dto.openAi.thread;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RespDto {

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Builder
    public static class ThreadRespDto{
        private String id;
        private String object;

        @JsonProperty("created_at")
        private Long createdAt;

        private Object metadata;
    }
    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Builder
    public static class DeleteThreadRespDto{
        private String id;
        private String object;
        private boolean deleted;
    }
}
