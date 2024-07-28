package com.develokit.maeum_ieum.dto.elderly;

import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.EmergencyContactInfo;
import com.develokit.maeum_ieum.util.CustomUtil;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @Getter
    @NoArgsConstructor
    public static class ElderlyCreateReqDto{
        @NotNull
        @Size(min = 2, message = "이름은 2글자 이상이여야 합니다")
        private String name; //이름
        @Pattern(regexp = "^(FEMALE|MALE)$", message = "성별은 FEMALE 또는 MALE이어야 합니다")
        private Gender gender; //성별
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd이어야 합니다.")
        private String birthDate; //생년월일
        @Size(min = 1, message = "주거지를 입력해야 합니다")
        private String homeAddress; //주거지
        @Pattern(regexp = "^(010-\\d{4}-\\d{4})|(02-\\d{3,4}-\\d{4})|(0[3-9]\\d-\\d{3,4}-\\d{4})$", message = "유효한 전화번호 형식이 아닙니다")
        private String contact; //연락처
        private String healthInfo; //특이 사항
        private MultipartFile imgFile;

        //긴급 연락처
        private String emergencyName;
        private String emergencyContact;
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
