package com.develokit.maeum_ieum.config.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtVo {

    public static String SECRET;

    @Value("${jwt.secret}")
    public void setSECRET(String secret) {
        JwtVo.SECRET = secret;
    }

    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    public static String TOKEN_PREFIX = "Bearer ";

    public static final String HEADER = "Authorization";
}
