package com.develokit.maeum_ieum.dto.caregiver;

import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RespDto {

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
