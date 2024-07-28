package com.develokit.maeum_ieum.dto.loginUser;

import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RespDto {


    @NoArgsConstructor
    @Getter
    public static class LoginRespDto{
        private String username;

        public LoginRespDto(LoginUser loginUser) {
            this.username = loginUser.getUsername();
        }
    }
}
