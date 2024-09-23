package com.develokit.maeum_ieum.dto.openAi.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.aspectj.bridge.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RespDto {

    @NoArgsConstructor
    @Getter
    public static class ListMessageRespDto{
        LinkedList<MessageRespDto> data;
    }

    @Getter
    @NoArgsConstructor
    @ToString
    public static class MessageRespDto {
        private String id;
        private String object;
        @JsonProperty("created_at")
        private Long createdAt;
        @JsonProperty("thread_id")
        private String threadId;
        private String role;
        private String status;
        private List<ContentDto> content;

        @JsonProperty("assistant_id")
        private String assistantId;

        @JsonProperty("run_id")
        private String runId;
        private Object metadata;

        @Getter
        @NoArgsConstructor
        @ToString
        public static class ContentDto{
            private String type;
            private TextDto text;

            @Getter
            @NoArgsConstructor
            @ToString
            public static class TextDto{
                private String value;
            }
        }

    }
}
