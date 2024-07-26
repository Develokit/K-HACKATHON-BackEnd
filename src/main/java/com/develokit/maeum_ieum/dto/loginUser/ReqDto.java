package com.develokit.maeum_ieum.dto.loginUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ReqDto {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class LoginReqDto{
        private String username;
        private String password;
    }
}
