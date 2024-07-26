package com.develokit.maeum_ieum.dto.caregiver;

import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class ReqDto {
    @Getter
    @NoArgsConstructor
    public static class JoinReqDto{
        private String username;
        private String password;
        private String contact;
        private Gender gender;
        private LocalDate birthDate;
        private String organization;
        private MultipartFile img;
        private String name;
        @JsonIgnore
        private String imgUrl;

        public Caregiver toEntity(BCryptPasswordEncoder bCryptPasswordEncoder, String imgUrl){
            return Caregiver.builder()
                    .username(username)
                    .password(bCryptPasswordEncoder.encode(password))
                    .organization(organization)
                    .birthDate(birthDate)
                    .gender(gender)
                    .contact(contact)
                    .name(name)
                    .imgUrl(imgUrl)
                    .build();
        }

    }
}
