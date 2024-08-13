package com.develokit.maeum_ieum.dto.elderly;

import com.amazonaws.endpointdiscovery.DaemonThreadFactory;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.EmergencyContactInfo;
import com.develokit.maeum_ieum.util.CustomUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class ReqDto {

    /*
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
    @AllArgsConstructor
    @Builder
    public static class ElderlyCreateReqDto{
        @NotNull
        @Size(min = 2, message = "이름은 2글자 이상이여야 합니다")
        @Schema(description = "노인 이름: 최소 2글자 이상")
        private String name; //이름

        @Schema(description = "노인 성별: FEMALE 또는 MALE")
        @Pattern(regexp = "^(FEMALE|MALE)$", message = "성별은 FEMALE 또는 MALE이어야 합니다")
        private String gender; //성별

        @Schema(description = "노인 생년월일: yyyy-MM-dd 형식 준수")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd이어야 합니다.")
        private String birthDate; //생년월일

        @Schema(description = "노인 거주 주소: 최소 1글자 이상")
        @Size(min = 1, message = "주거지를 입력해야 합니다")
        private String homeAddress; //주거지

        @Schema(description = "노인 전화번호: 010으로 시작하는 전화번호 || 지역번호가 포함된 전화번호 형식 준수")
        @Pattern(regexp = "^(010-\\d{4}-\\d{4})|(02-\\d{3,4}-\\d{4})|(0[3-9]\\d-\\d{3,4}-\\d{4})$", message = "유효한 전화번호 형식이 아닙니다")
        private String contact; //연락처

        @Schema(description = "노인 건강 특이 사항")
        private String healthInfo; //특이 사항

        @Schema(description = "노인 프로필 사진")
        private MultipartFile imgFile;

        //긴급 연락처
        @Schema(description = "긴급 연락 대상 이름")
        private String emergencyName;

        @Schema(description = "긴급 연락 대상 전화번호: 010으로 시작하는 전화번호 || 지역번호가 포함된 전화번호 형식 준수")
        @Pattern(regexp = "^(010-\\d{4}-\\d{4})|(02-\\d{3,4}-\\d{4})|(0[3-9]\\d-\\d{3,4}-\\d{4})$", message = "유효한 전화번호 형식이 아닙니다")
        private String emergencyContact;

        @Schema(description = "긴급 연락 대상과의 관계")
        private String relationship;


        public Elderly toEntity(Caregiver caregiver, String imgUrl){
            return Elderly.builder()
                    .name(name)
                    .homeAddress(homeAddress)
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
