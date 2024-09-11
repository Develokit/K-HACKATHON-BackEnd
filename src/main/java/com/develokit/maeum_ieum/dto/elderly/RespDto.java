package com.develokit.maeum_ieum.dto.elderly;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.message.Message;
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

import static com.develokit.maeum_ieum.dto.openAi.message.RespDto.*;

public class RespDto {
    @Getter
    @AllArgsConstructor
    @Schema(description = "주간 보고서 생성 요일 변경 응답 DTO")
    public static class ElderlyReportDayModifyRespDto {
        public ElderlyReportDayModifyRespDto(Elderly elderly){
            this.elderlyId = elderly.getId();
            this.reportDay = elderly.getReportDay().toString();
        }

        @Schema(description = "노인 아이디")
        private Long elderlyId;

        @Schema(description = "변경된 주간 보고서 생성 요일")
        private String reportDay;
    }


    @Getter
    @NoArgsConstructor
    public static class ElderlyInfoRespDto{


        public ElderlyInfoRespDto(Elderly elderly) {
            this.accessCode = elderly.getAssistant()==null?"AI 어시스턴트를 생성해주세요":elderly.getAssistant().getAccessCode();
            this.name = elderly.getName();
            this.gender = elderly.getGender().toString().equals("MALE")?"남":"여";
            this.birthDate = CustomUtil.BirthDateToString(elderly.getBirthDate());
            this.homeAddress = elderly.getHomeAddress();
            this.organization = elderly.getOrganization();
            this.contact = elderly.getContact();
            this.healthInfo = elderly.getHealthInfo();
            this.emergencyName = elderly.getEmergencyContactInfo().getEmergencyName();
            this.emergencyContact = elderly.getEmergencyContactInfo().getEmergencyContact();
            this.relationship = elderly.getEmergencyContactInfo().getRelationship();
            this.assistantName = elderly.getAssistant()!=null?elderly.getAssistant().getName():null;
            this.reportDay = elderly.getReportDay().toString();
            this.assistantId = elderly.getAssistant()==null?null:elderly.getAssistant().getId();
            this.imgUrl = elderly.getImgUrl();
        }

        @Schema(description = "노인 이름: 최소 2글자 이상")
        @Nullable
        private String name; //이름
        @Schema(description = "노인 프로필 사진")
        @Nullable
        private String imgUrl;

        @Schema(description = "노인 접속 코드: AI 어시스턴트가 존재하지 않을 시 'AI 어시스턴트를 생성해주세요' 라는 문구가 반환")
        private String accessCode;

        @Schema(description = "어시스턴트 아이디")
        private Long assistantId;

        @Schema(description = "노인 성별: FEMALE 또는 MALE")
        @Nullable
        private String gender; //성별

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
            this.birthDate = CustomUtil.BirthDateToString(elderly.getBirthDate());
            this.homeAddress = elderly.getHomeAddress();
            this.organization = elderly.getOrganization();
            this.contact = elderly.getContact();
            this.healthInfo = elderly.getHealthInfo();
            this.emergencyName = elderly.getEmergencyContactInfo().getEmergencyName();
            this.emergencyContact = elderly.getEmergencyContactInfo().getEmergencyContact();
            this.relationship = elderly.getEmergencyContactInfo().getRelationship();
            this.assistantName = elderly.getAssistant()==null?null:elderly.getAssistant().getName();
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
    @Schema(description = "채팅방 진입 요청에 따른 DTO")
    public static class CheckAssistantInfoRespDto{
        @Schema(description = "스레드 아이디")
        private String threadId;
        @Schema(description = "AI 어시스턴트 이름")
        private String assistantName;
        @Schema(description = "OpenAI 요청을 위한 openAiAssistantId")
        private String openAiAssistantId;
        @Schema(description = "채팅 내역")
        private ChatInfoDto chatInfo;

        @NoArgsConstructor
        @Getter
        static class ChatInfoDto{
            @Schema(description = "반환 채팅 수")
            private int size;
            @Schema(description = "채팅 내역")
            private final LinkedList<ChatDto> chat = new LinkedList<>();

            public ChatInfoDto(List<Message> messageList){
                this.size = messageList.size();
                for (Message message : messageList) {
                    this.chat.add(new ChatDto(message));
                }
            }
            @NoArgsConstructor
            @Getter
            static class ChatDto{
                @Schema(description = "답변 주체: USER | AI")
                private String role;
                @Schema(description = "답변 주체의 메시지")
                private String content;
                @Schema(description = "메시지 발행 시간")
                private String timeStamp;

                public ChatDto(Message message){
                    this.role = message.getMessageType().toString();
                    this.content = message.getContent();
                    this.timeStamp = CustomUtil.LocalDateTimeFormatForChatResponse(message.getCreatedDate());
                }
            }
        }

//            @NoArgsConstructor
//            @Getter
//            static class ChatDto{
//                @Schema(description = "답변 주체: USER | AI")
//                private String role;
//                @Schema(description = "답변 주체의 메시지")
//                private String content;
//
//                public ChatDto(MessageRespDto messageRespDto){
//                    this.role = messageRespDto.getRole();
//                    this.content = messageRespDto.getContent().get(0).getText().getValue().toString();
//                }
//            }
//        }

//        @NoArgsConstructor
//        @Getter
//        static class ChatInfoDto{
//            @Schema(description = "반환 채팅 수")
//            private int size;
//            @Schema(description = "채팅 내역")
//            private LinkedList<ChatDto> chat = new LinkedList<>();
//
//            public ChatInfoDto(List<MessageRespDto> data){
//                this.size = data.size();
//                for (MessageRespDto messageDto : data) {
//                    this.chat.add(new ChatDto(messageDto));
//                }
//            }
//
//            @NoArgsConstructor
//            @Getter
//            static class ChatDto{
//                @Schema(description = "답변 주체: USER | AI")
//                private String role;
//                @Schema(description = "답변 주체의 메시지")
//                private String content;
//
//                public ChatDto(MessageRespDto messageRespDto){
//                    this.role = messageRespDto.getRole();
//                    this.content = messageRespDto.getContent().get(0).getText().getValue().toString();
//                }
//            }
//        }

//        public CheckAssistantInfoRespDto(Assistant assistant, List<MessageRespDto> messageRespDto) {
//            this.threadId = assistant.getThreadId();
//            this.assistantName = assistant.getName();
//            this.openAiAssistantId = assistant.getOpenAiAssistantId();
//            if(messageRespDto != null)
//                this.chatInfo = new ChatInfoDto(messageRespDto);
//        }
        public CheckAssistantInfoRespDto(Assistant assistant, List<Message> messageList) {
            this.threadId = assistant.getThreadId();
            this.assistantName = assistant.getName();
            this.openAiAssistantId = assistant.getOpenAiAssistantId();
            if(!messageList.isEmpty())
                this.chatInfo = new ChatInfoDto(messageList);
        }
    }

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Builder
    public static class ElderlyMainRespDto{
        @Schema(description = "요양사 이름")
        private String caregiverName;
        @Schema(description = "요양사 아이디")
        private Long caregiverId;
        @Schema(description = "요양사 연락처")
        private String caregiverContact;
        @Schema(description = "요양사 소속 조직")
        private String caregiverOrganization;
        @Schema(description = "요양사 프로필 사진")
        private String caregiverImgUrl;
        @Schema(description = "노인 이름")
        private String elderlyName;
        @Schema(description = "노인 생년월일")
        private String elderlyBirthdate;
        @Schema(description = "노인 프로필 사진")
        private String elderlyImgUrl;
        @Schema(description = "마지막 대화 ex) 1시간 전이면 1로 나감, 채팅 기록이 없으면 null")
        private Long lastChatDate; //마지막 대화 'n시간 전'
        @Schema(description = "노인 이름")
        private int age;
        @Schema(description = "AI 어시스턴트 아이디")
        private Long assistantId; //DB 어시스턴트 아이디
        public ElderlyMainRespDto(Caregiver caregiver, Elderly elderly){
            this.caregiverContact = caregiver.getContact();
            this.caregiverImgUrl = caregiver.getImgUrl();
            this.caregiverOrganization = caregiver.getOrganization();
            this.caregiverName = caregiver.getName();
            this.elderlyName = elderly.getName();
            this.elderlyImgUrl = elderly.getImgUrl();
            this.elderlyBirthdate = CustomUtil.BirthDateToString(elderly.getBirthDate());
            this.age = CustomUtil.calculateAge(elderly.getBirthDate());
            this.lastChatDate = CustomUtil.calculateHoursAgo(elderly.getLastChatTime());
            this.assistantId = elderly.getAssistant().getId();
            this.caregiverId = caregiver.getId();
        }

    }

    @NoArgsConstructor
    @Getter
    public static class ElderlyCreateRespDto{
        @Schema(description = "노인 아이디")
        private Long id;
        @Schema(description = "노인 등록 날짜")
        private LocalDateTime createdDate;

        public ElderlyCreateRespDto(Elderly elderly){
            this.id = elderly.getId();
            this.createdDate = elderly.getCreatedDate();
        }
    }
}
