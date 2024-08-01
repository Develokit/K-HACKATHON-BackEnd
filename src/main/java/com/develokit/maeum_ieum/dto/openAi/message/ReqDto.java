package com.develokit.maeum_ieum.dto.openAi.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class ReqDto {

    @Getter
    public static class ContentDto{ //프론트에서 받는 컨텐트 -> 이거 꺼내서 CreateMessageReqDto에 삽입
        @NotNull
        private String content;
    }

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

        public CreateMessageReqDto(String role, String content){
            this.role = role;
            this.content = content;
        }

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
