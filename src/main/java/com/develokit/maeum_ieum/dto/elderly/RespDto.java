package com.develokit.maeum_ieum.dto.elderly;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.util.CustomUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class RespDto {
    @NoArgsConstructor
    @Getter
    public static class CheckAssistantInfoRespDto{
        private String threadId;
        private String assistantName;
        private String openAiAssistantId;
        private ChatInfoDto chatInfo;

        @NoArgsConstructor
        @Getter
        static class ChatInfoDto{
            private int size;
            private LinkedList<ChatDto> chat = new LinkedList<>();

            public ChatInfoDto(List<com.develokit.maeum_ieum.dto.openAi.message.RespDto.MessageRespDto> data){
                this.size = data.size();
                for (com.develokit.maeum_ieum.dto.openAi.message.RespDto.MessageRespDto messageDto : data) {
                    this.chat.add(new ChatDto(messageDto));
                }
            }

            @NoArgsConstructor
            @Getter
            static class ChatDto{
                private String role;
                private String content;

                public ChatDto(com.develokit.maeum_ieum.dto.openAi.message.RespDto.MessageRespDto messageRespDto){
                    this.role = messageRespDto.getRole();
                    this.content = messageRespDto.getContent().get(0).getText().getValue().toString();
                }
            }
        }

        public CheckAssistantInfoRespDto(Assistant assistant, List<com.develokit.maeum_ieum.dto.openAi.message.RespDto.MessageRespDto> messageRespDto) {
            this.threadId = assistant.getThreadId();
            this.assistantName = assistant.getName();
            this.openAiAssistantId = assistant.getOpenAiAssistantId();
            if(messageRespDto != null)
                this.chatInfo = new ChatInfoDto(messageRespDto);
        }
    }

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Builder
    public static class MainHomeRespDto{
        private String caregiverName;
        private String caregiverContact;
        private String caregiverOrganization;
        private String caregiverImgUrl;
        private String elderlyName;
        private LocalDate elderlyBirthdate;
        private String elderlyImgUrl;
        private Long lastChatDate; //마지막 대화 'n시간 전'
        private int age;
        public MainHomeRespDto(Caregiver caregiver, Elderly elderly){
            this.caregiverContact = caregiver.getContact();
            this.caregiverImgUrl = caregiver.getImgUrl();
            this.caregiverOrganization = caregiver.getOrganization();
            this.caregiverName = caregiver.getName();
            this.elderlyName = elderly.getName();
            this.elderlyImgUrl = elderly.getImgUrl();
            this.elderlyBirthdate = elderly.getBirthDate();
            this.age = CustomUtil.calculateAge(elderlyBirthdate);
            this.lastChatDate = CustomUtil.calculateHoursAgo(elderly.getLastChatTime());
        }

    }
    @NoArgsConstructor
    @Getter
    public static class ElderlyCreateRespDto{
        private Long id;
        private String name;
        private String contact;
        private LocalDateTime createDate;

        public ElderlyCreateRespDto(Elderly elderly){
            this.id = elderly.getId();
            this.name = elderly.getName();
            this.contact = elderly.getContact();
            this.createDate = elderly.getCreatedDate();
        }
    }
}
