package com.develokit.maeum_ieum.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;

import java.util.Date;


public class JwtProvider {
    public static String create(LoginUser loginUser){ //토큰 생성
        String jwtToken = JWT.create()
                .withSubject("CareGiver: jwt")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVo.EXPIRATION_TIME))
                .withClaim("id", loginUser.getUsername())
                .withClaim("role", loginUser.getCaregiver().getRole().toString())
                .sign(Algorithm.HMAC256(JwtVo.SECRET));

        System.out.println("토큰에 넣은 role 값 = " + loginUser.getCaregiver().getRole().toString());

        return JwtVo.TOKEN_PREFIX + jwtToken;
    }

    public static LoginUser verify(String token){ //토큰 검증 -> 리턴하는 LoginUser를 강제로 시큐리티 세션에 주입

        DecodedJWT decodedJWT =
                JWT.require(Algorithm.HMAC256(JwtVo.SECRET)).build().verify(token);

        String username =
                decodedJWT.getClaim("id").asString();
        String role = decodedJWT.getClaim("role").asString();


        Caregiver caregiver =
                Caregiver.builder().username(username).role(Role.valueOf(role)).build();
        System.out.println("caregiver.getRole() = " + caregiver.getRole());

        return new LoginUser(caregiver);
    }



}
