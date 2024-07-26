package com.develokit.maeum_ieum.config.jwt;

public interface JwtVo {
    public static final String SECRET = "Maeum-Ieum";  //HS256(대칭키) 사용
    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; //일주일
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
}
