package com.develokit.maeum_ieum.dto.caregiver;

import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "요앙사 회원가입 요청을 위한 DTO")
public class ReqDto {

    @Getter
    @NoArgsConstructor
    @Setter
    public static class JoinReqDto{
        @Schema(description = "요양사 아이디: 영문자+숫자 포함 6~12자 사이")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,12}$", message = "영문자와 숫자를 포함하여 6자에서 12자 사이여야 합니다")
        private String username; //영문자+숫자 조합 최소 6~12자

        @Schema(description = "요양사 비밀번호: 영문자+숫자 포함 6~12자 사이", required = true)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,12}$", message = "영문자와 숫자를 포함하여 6자에서 12자 사이여야 합니다")
        private String password; //영문자+숫자 조합 최소 6~12자

        @Schema(description = "요양사 전화번호: 010으로 시작하는 전화번호 || 지역번호가 포함된 전화번호 형식 준수", required = true)
        @Pattern(regexp = "^(010-\\d{4}-\\d{4})|(02-\\d{3,4}-\\d{4})|(0[3-9]\\d-\\d{3,4}-\\d{4})$", message = "유효한 전화번호 형식이 아닙니다")
        private String contact; // '-'를 포함하는 전화번호

        @Schema(description = "요양사 성별: FEMALE 또는 MALE", required = true)
        @Pattern(regexp = "^(FEMALE|MALE)$", message = "성별은 FEMALE 또는 MALE이어야 합니다")
        private String gender;

        @Schema(description = "요양사 생년월일: yyyy-MM-dd 형식 준수", required = true)
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd이어야 합니다.")
        private String birthDate;

        @Schema(description = "요양사 소속 기관명: 최소 1글자 이상", required = true)
        @Size(min = 1, message = "기관명을 입력해야 합니다")
        private String organization;

        @Schema(description = "요양사 프로필 사진")
        private MultipartFile img;

        @Schema(description = "요양사 이름: 최소 2글자 이상", required = true)
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
