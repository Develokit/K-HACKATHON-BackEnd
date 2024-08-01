package com.develokit.maeum_ieum.dto.openAi.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RespDto {

    @NoArgsConstructor
    @Getter
    public static class StreamRunRespDto{
        private String event;
        private StreamRunDataRespDto data;

        @NoArgsConstructor
        @Getter
        public static class StreamRunDataRespDto{
            private String id;
            private String object;
            private DeltaDto delta;

            @NoArgsConstructor
            @Getter
            public static class DeltaDto{
                private int index;
                private String type;
                private TextDto text;

                @NoArgsConstructor
                @Getter
                public static class TextDto{
                    private String value;
                }
            }
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ListRunRespDto{
        List<RunRespDto> runRespDtoList = new ArrayList<>();
    }


    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RunRespDto{
        private String id;
        private String object;
        private Long createdAt;
        private String assistantId;
        private String threadId;
        private String status;
        private Long startedAt;
        private Long expiresAt;
        private Long cancelledAt;
        private Long failedAt;
        private Long completedAt;
        private Object lastError;
        private String model;
        private Object instructions;

    }
}
