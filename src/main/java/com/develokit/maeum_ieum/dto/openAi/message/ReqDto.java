package com.develokit.maeum_ieum.dto.openAi.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class ReqDto {

    @Getter
    public static class CreateMessageReqDto {
        @NotNull
        private String role;
        @NotNull
        private String content;

        @Nullable
        private List<AttachmentDto> attachments;
        @Nullable
        private Object metadata;

        @Getter
        public static class AttachmentDto{
            @JsonProperty("file_id")
            private String fileId;
            private List<ToolDto> tools;

        }
    }
    public static class ToolDto{
        private String type;
    }
}
