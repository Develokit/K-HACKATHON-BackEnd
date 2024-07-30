package com.develokit.maeum_ieum.dto.caregiver;

import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RespDto {



    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MyInfoRespDto{
        private String name;
        private String imgUrl;
        private Gender gender;
        private LocalDate birthDate;
        private String organization;
        private String contact;

        public MyInfoRespDto(Caregiver caregiver){
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
