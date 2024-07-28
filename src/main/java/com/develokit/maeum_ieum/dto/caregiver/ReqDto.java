package com.develokit.maeum_ieum.dto.caregiver;

import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReqDto {
    @Getter
    @NoArgsConstructor
    @Setter
    public static class JoinReqDto{
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,12}$", message = "영문자와 숫자를 포함하여 6자에서 12자 사이여야 합니다")
        private String username; //영문자+숫자 조합 최소 6~12자
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,12}$", message = "영문자와 숫자를 포함하여 6자에서 12자 사이여야 합니다")
        private String password; //영문자+숫자 조합 최소 6~12자
        @Pattern(regexp = "^(010-\\d{4}-\\d{4})|(02-\\d{3,4}-\\d{4})|(0[3-9]\\d-\\d{3,4}-\\d{4})$", message = "유효한 전화번호 형식이 아닙니다")
        private String contact; // '-'를 포함하는 전화번호
        @Pattern(regexp = "^(FEMALE|MALE)$", message = "성별은 FEMALE 또는 MALE이어야 합니다")
        private String gender;
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd이어야 합니다.")
        private String birthDate;
        @Size(min = 1, message = "기관명을 입력해야 합니다")
        private String organization;
        private MultipartFile img;
        @NotNull
        @Size(min = 2, message = "이름은 2글자 이상이여야 합니다")
        private String name;

        public Caregiver toEntity(BCryptPasswordEncoder bCryptPasswordEncoder, String imgUrl){
            return Caregiver.builder()
                    .username(username)
                    .password(bCryptPasswordEncoder.encode(password))
                    .organization(organization)
                    .birthDate(CustomUtil.StringToLocalDate(birthDate))
                    .gender(Gender.valueOf(gender))
                    .contact(contact)
                    .name(name)
                    .role(Role.ADMIN)
                    .imgUrl(imgUrl)
                    .build();
        }

    }
}
