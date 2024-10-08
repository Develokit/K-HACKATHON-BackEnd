package com.develokit.maeum_ieum.dto.caregiver;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.util.CustomUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DecimalStyle;
import java.util.List;
import java.util.stream.Collectors;

public class RespDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "요양사 회원가입 시 유저네임 중복 체크 여부 반환 DTO")
    public static class CaregiverDuplicatedRespDto {
        @Schema(description = "검사한 사용자 아이디")
        private String username;

        @Schema(description = "아이디 중복 여부")
        private boolean isDuplicated;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "요양사 마이 페이지에서 수정된 이미지 경로 반환 DTO")
    public static class CaregiverImgModifyRespDto{
        private String imgUrl;
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "요양사 마이 페이지에서 수정된 데이터 반환 DTO")
    public static class CaregiverModifyRespDto{
        @Schema(description = "요양사 이름")
        private String name;
        @Schema(description = "요양사 프로필 사진")
        private String imgUrl;
        @Schema(description = "요양사 성별")
        private Gender gender;
        @Schema(description = "요양사 생년월일")
        private LocalDate birthDate;
        @Schema(description = "요양사 소속")
        private String organization;
        @Schema(description = "요양사 연락처")
        private String contact;

        public CaregiverModifyRespDto(Caregiver caregiver){
            this.name = caregiver.getName();
            this.imgUrl = caregiver.getImgUrl();
            this.gender = caregiver.getGender();
            this.birthDate = caregiver.getBirthDate();
            this.organization = caregiver.getOrganization();
            this.contact = caregiver.getContact();
        }
    }



    @Getter
    @NoArgsConstructor
    @Schema(description = "요양사 메인 페이지 데이터 반환 DTO")
    public static class CaregiverMainRespDto {
        @Schema(description = "요양사 이름")
        private String name;
        @Schema(description = "요양사 프로필 사진")
        private String img;
        @Schema(description = "총 관리 인원")
        private int totalCareNumber;
        @Schema(description = "요양사 소속 기관명")
        private String organization;
        @Schema(description = "요양사 담당 노인 사용자 리스트")
        private List<ElderlyInfoDto> elderlyInfoDto;
        @Schema(description = "다음 페이지 로드를 위한 커서")
        private String nextCursor;


        public CaregiverMainRespDto(Caregiver caregiver, List<Elderly>elderlyList, String nextCursor){
            this.name = caregiver.getName();
            this.img = caregiver.getImgUrl();
            this.totalCareNumber = caregiver.getElderlyList().size();
            this.organization = caregiver.getOrganization();
            this.elderlyInfoDto = elderlyList.stream()
                    .map(ElderlyInfoDto::new)
                    .collect(Collectors.toList());
            this.nextCursor = nextCursor;
        }

        @NoArgsConstructor
        @Getter
        @Schema(description = "요양사 메인 페이지에서 반환되는 노인 사용자 DTO")
        public static class ElderlyInfoDto{
            @Schema(description = "노인 이름")
            private String name;
            @Schema(description = "노인 나이")
            private int age;
            @Schema(description = "노인 연락처")
            private String contact;

            @Schema(description = "노인 주소")
            private String homeAddress;
            @Schema(description = "마지막 대화 시간: 마지막 대화 시간이 있다면 'n시간 전' 으로 나가고, 마지막 대화 시간이 없으면 '없음'으로 나감")
            private String lastChatTime;
            @Schema(description = "노인 사용자 아이디")
            private Long elderlyId;
            @Schema(description = "어시스턴트 생성 여부: 있으면 true, 없으면 false")
            private boolean hasAssistant;
            @Schema(description = "AI 어시스턴트 아이디")
            private Long assistantId;

            @Schema(description = "노인 접속 코드: AI 어시스턴트가 존재하지 않을 시 'AI 어시스턴트를 생성해주세요' 라는 문구가 반환")
            private String accessCode;

            @Schema(description = "AI 어시스턴트 이름")
            private String assistantName;
            @Schema(description = "노인 프로필 사진")
            private String img;


            public ElderlyInfoDto(Elderly elderly){
                this.name = elderly.getName();
                this.age = CustomUtil.calculateAge(elderly.getBirthDate());
                this.contact = elderly.getContact();
                this.lastChatTime = elderly.getLastChatTime() == null ? "없음": CustomUtil.calculateHoursAgo(elderly.getLastChatTime())+"시간 전";
                this.elderlyId = elderly.getId();
                this.hasAssistant = elderly.hasAssistant();
                this.assistantId = hasAssistant?elderly.getAssistant().getId():null;
                this.assistantName =hasAssistant?elderly.getAssistant().getName():null;
                this.img = elderly.getImgUrl();
                this.accessCode = hasAssistant?elderly.getAccessCode():"AI 어시스턴트를 생성해주세요";
                this.homeAddress = elderly.getHomeAddress();
            }

        }
    }



    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Schema(description = "요양사 마이 페이지에서 반환되는 DTO")
    public static class MyInfoRespDto{
        @Schema(description = "요양사 이름")
        private String name;
        @Schema(description = "요양사 프로필 사진")
        private String imgUrl;
        @Schema(description = "요양사 성별")
        private Gender gender;
        @Schema(description = "요양사 생년월일")
        private String birthDate;
        @Schema(description = "요양사 소속")
        private String organization;
        @Schema(description = "요양사 연락처")
        private String contact;

        public MyInfoRespDto(Caregiver caregiver){
            this.name = caregiver.getName();
            this.imgUrl = caregiver.getImgUrl();
            this.gender = caregiver.getGender();
            this.birthDate = CustomUtil.BirthDateToString(caregiver.getBirthDate());
            this.organization = caregiver.getOrganization();
            this.contact = caregiver.getContact();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class JoinRespDto{
        private String name;
        private String username;
        private LocalDateTime createDate;

        public JoinRespDto(Caregiver caregiver){
            this.name = caregiver.getName();
            this.username = caregiver.getUsername();
            this.createDate = caregiver.getCreatedDate();
        }
    }
}
