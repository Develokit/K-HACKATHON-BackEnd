package com.develokit.maeum_ieum.dto.loginUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReqDto {

    @Getter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class LoginReqDto{
        private String username;
        private String password;
    }
}
