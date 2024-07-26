package com.develokit.maeum_ieum.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;

import java.util.Date;


public class JwtProvider {
    public static String create(LoginUser loginUser){ //토큰 생성
        String jwtToken = JWT.create()
                .withSubject("CareGiver: jwt")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVo.EXPIRATION_TIME))
                .withClaim("id", loginUser.getUsername())
                .sign(Algorithm.HMAC256(JwtVo.SECRET));


        return JwtVo.TOKEN_PREFIX + jwtToken;
    }

    public static LoginUser verify(String token){ //토큰 검증 -> 리턴하는 LoginUser를 강제로 시큐리티 세션에 주입

        DecodedJWT decodedJWT =
                JWT.require(Algorithm.HMAC256(JwtVo.SECRET)).build().verify(token);
        String username =
                decodedJWT.getClaim("id").toString();
        Caregiver caregiver =
                Caregiver.builder().username(username).build();
        return new LoginUser(caregiver);
    }



}
