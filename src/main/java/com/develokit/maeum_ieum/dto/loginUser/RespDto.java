package com.develokit.maeum_ieum.dto.loginUser;

import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import lombok.AllArgsConstructor;

public class RespDto {


    public static class LoginRespDto{
        private String username;

        public LoginRespDto(LoginUser loginUser) {
            this.username = loginUser.getUsername();
        }
    }
}
