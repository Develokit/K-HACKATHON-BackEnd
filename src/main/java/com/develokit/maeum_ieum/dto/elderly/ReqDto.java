package com.develokit.maeum_ieum.dto.elderly;

import com.amazonaws.endpointdiscovery.DaemonThreadFactory;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.EmergencyContactInfo;
import com.develokit.maeum_ieum.util.CustomUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class ReqDto {

    @Getter
    @NoArgsConstructor
    @Schema(description = "노인 사용자 이미지 수정 요청을 위한 DTO")
    public static class ElderlyImgModifyReqDto{
        private MultipartFile img;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "노인 사용자 수정 요청을 위한 DTO")
    public static class ElderlyModifyReqDto{

        @Size(min = 2, message = "이름은 2글자 이상이여야 합니다")
        @Schema(description = "노인 이름: 최소 2글자 이상")
        @Nullable
        private String name; //이름

        @Schema(description = "노인 성별: FEMALE 또는 MALE")
        @Pattern(regexp = "^(FEMALE|MALE)$", message = "성별은 FEMALE 또는 MALE이어야 합니다")
        @Nullable
        private String gender; //성별

        @Schema(description = "노인 생년월일: yyyy-MM-dd 형식 준수")
        @Nullable
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd이어야 합니다.")
        private String birthDate; //생년월일

        @Schema(description = "노인 거주 주소: 최소 1글자 이상")
        @Size(min = 1, message = "주거지를 입력해야 합니다")
        @Nullable
        private String homeAddress; //주거지

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

    /* 노인 사용자 생성 요청 필드
        - 이름
        - 성별
        - 생년월일
        -주거지
        -긴급 연락처(이름, 전화번호, 노인과의 관계)
        - 노인 연락처
        - 노인 특이사항
     */

    @Schema(description = "노인 사용자 생성 요청을 위한 DTO")
    @Getter
    @NoArgsConstructor
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ElderlyCreateReqDto{

        @Size(min = 2)
        @Pattern(regexp = "^.{2,}$", message = "이름은 최소 2글자 이상이여야 합니다")
        @Schema(description = "노인 이름: 최소 2글자 이상")
        @NotBlank
        private String name; //이름

        @Schema(description = "노인 성별: FEMALE 또는 MALE")
        @Pattern(regexp = "^(FEMALE|MALE)$", message = "성별은 FEMALE 또는 MALE이어야 합니다")
        private String gender; //성별

        @Schema(description = "노인 생년월일: yyyy-MM-dd 형식 준수")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd이어야 합니다.")
        private String birthDate; //생년월일

        @Schema(description = "노인 거주 주소: 최소 1글자 이상")
        @Size(min = 1, message = "주거지는 최소 1글자 이상이여야 합니다")
        @Pattern(regexp = "^.{1,}$", message = "주거지를 입력해야 합니다")
        private String homeAddress; //주거지

        @Schema(description = "노인 전화번호: 010으로 시작하는 전화번호 || 지역번호가 포함된 전화번호 형식 준수")
        @Pattern(regexp = "^(010-\\d{4}-\\d{4})|(02-\\d{3,4}-\\d{4})|(0[3-9]\\d-\\d{3,4}-\\d{4})$", message = "유효한 전화번호 형식이 아닙니다")
        private String contact; //연락처

        @Schema(description = "노인 건강 특이 사항")
        private String healthInfo; //특이 사항

        @Schema(description = "노인 프로필 사진")
        private MultipartFile img;

        //긴급 연락처
        @Schema(description = "긴급 연락 대상 이름")
        @Nullable
        private String emergencyName;

        @Schema(description = "긴급 연락 대상 전화번호: 010으로 시작하는 전화번호 || 지역번호가 포함된 전화번호 형식 준수")
        @Pattern(regexp = "^(010-\\d{4}-\\d{4})|(02-\\d{3,4}-\\d{4})|(0[3-9]\\d-\\d{3,4}-\\d{4})$", message = "유효한 전화번호 형식이 아닙니다")
        @Nullable
        private String emergencyContact;

        @Schema(description = "긴급 연락 대상과의 관계")
        @Nullable
        private String relationship;


        public Elderly toEntity(Caregiver caregiver, String imgUrl){
            return Elderly.builder()
                    .name(name)
                    .homeAddress(homeAddress)
                    .gender(Gender.valueOf(gender))
                    .contact(contact)
                    .organization(caregiver.getOrganization())
                    .emergencyContactInfo(EmergencyContactInfo.builder()
                            .emergencyContact(emergencyContact)
                            .emergencyName(emergencyName)
                            .relationship(relationship)
                    .build())
                    .caregiver(caregiver)
                    .birthDate(CustomUtil.StringToLocalDate(birthDate))
                    .healthInfo(healthInfo)
                    .imgUrl(imgUrl)
                    .role(Role.USER)
                    .build();
        }
    }
}
