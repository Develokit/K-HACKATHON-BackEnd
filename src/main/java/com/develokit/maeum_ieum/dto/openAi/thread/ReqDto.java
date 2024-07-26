package com.develokit.maeum_ieum.dto.openAi.thread;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.develokit.maeum_ieum.dto.openAi.thread.ReqDto.CreateThreadReqDto.*;

public class ReqDto {

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Getter
    @NoArgsConstructor
    public static class CreateThreadAndRunReqDto {
        private String assistantId;
        private ThreadDto thread;

        public static class ThreadDto{
            private List<MessagesDto> messages;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CreateThreadReqDto{
        private List<MessagesDto> messages = new ArrayList<>();

        @Getter
        public static class MessagesDto{
            private String role;
            private String content;
        }
    }
}
