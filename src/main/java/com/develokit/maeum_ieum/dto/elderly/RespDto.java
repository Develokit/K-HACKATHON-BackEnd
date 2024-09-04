package com.develokit.maeum_ieum.dto.elderly;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.util.CustomUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class RespDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ElderlyImgModifyRespDto{
        private String imgUrl;
    }
    @Getter
    @NoArgsConstructor
    public static class ElderlyModifyRespDto{

        public ElderlyModifyRespDto(Elderly elderly) {
            this.name = elderly.getName();
            this.gender = elderly.getGender();
            this.birthDate = CustomUtil.BirthDateToString(elderly.getBirthDate()); // TODO xxxx년yy월ss일 -> 이렇게 반환하기!!
            this.homeAddress = elderly.getHomeAddress();
            this.organization = elderly.getOrganization();
            this.contact = elderly.getContact();
            this.healthInfo = elderly.getHealthInfo();
            this.emergencyName = elderly.getEmergencyContactInfo().getEmergencyName();
            this.emergencyContact = elderly.getEmergencyContactInfo().getEmergencyContact();
            this.relationship = elderly.getEmergencyContactInfo().getRelationship();
            this.assistantName = elderly.getAssistant().getName();
            this.reportDay = elderly.getReportDay().toString();
        }


        @Schema(description = "노인 이름: 최소 2글자 이상")
        @Nullable
        private String name; //이름

        @Schema(description = "노인 성별: FEMALE 또는 MALE")
        @Nullable
        private Gender gender; //성별

        @Schema(description = "노인 생년월일: yyyy-MM-dd 형식 준수")
        @Nullable
        private String birthDate; //생년월일

        @Schema(description = "노인 거주 주소: 최소 1글자 이상")
        @Nullable
        private String homeAddress; //주거지

        @Schema(description = "요양사 소속 기관명: 최소 1글자 이상")
        @Nullable
        private String organization; //소속 기관

        @Nullable
        @Schema(description = "노인 전화번호: 010으로 시작하는 전화번호 || 지역번호가 포함된 전화번호 형식 준수")
        @Pattern(regexp = "^(010-\\d{4}-\\d{4})|(02-\\d{3,4}-\\d{4})|(0[3-9]\\d-\\d{3,4}-\\d{4})$", message = "유효한 전화번호 형식이 아닙니다")
        private String contact; //연락처

        @Schema(description = "노인 건강 특이 사항")
        @Nullable
        private String healthInfo; //특이 사항
        //긴급 연락처
        @Schema(description = "긴급 연락 대상 이름")
        @Nullable
        private String emergencyName;

        @Nullable
        @Schema(description = "긴급 연락 대상 전화번호: 010으로 시작하는 전화번호 || 지역번호가 포함된 전화번호 형식 준수")
        @Pattern(regexp = "^(010-\\d{4}-\\d{4})|(02-\\d{3,4}-\\d{4})|(0[3-9]\\d-\\d{3,4}-\\d{4})$", message = "유효한 전화번호 형식이 아닙니다")
        private String emergencyContact;

        @Nullable
        @Schema(description = "긴급 연락 대상과의 관계")
        private String relationship;

        @Nullable
        @Schema(description = "AI Assistant 이름")
        private String assistantName;

        @Nullable
        @Schema(description = "주간 보고서 생성 요일: MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY")
        @Pattern(regexp = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY)$", message = "유효한 요일 형식이 아닙니다")
        private String reportDay; //주간 보고서 생성 요일
    }
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
    public static class ElderlyMainRespDto{
        private String caregiverName;
        private String caregiverContact;
        private String caregiverOrganization;
        private String caregiverImgUrl;
        private String elderlyName;
        private LocalDate elderlyBirthdate;
        private String elderlyImgUrl;
        private Long lastChatDate; //마지막 대화 'n시간 전'
        private int age;
        public ElderlyMainRespDto(Caregiver caregiver, Elderly elderly){
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
