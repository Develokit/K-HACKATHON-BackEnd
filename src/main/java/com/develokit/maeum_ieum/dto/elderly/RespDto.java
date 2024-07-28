package com.develokit.maeum_ieum.dto.elderly;

import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RespDto {

    @NoArgsConstructor
    public static class ElderlyCreateRespDto{
        private String name;
        private String contact;
        private LocalDateTime createDate;

        public ElderlyCreateRespDto(Elderly elderly){
            this.name = elderly.getName();
            this.contact = elderly.getContact();
            this.createDate = elderly.getCreatedDate();
        }
    }
}
