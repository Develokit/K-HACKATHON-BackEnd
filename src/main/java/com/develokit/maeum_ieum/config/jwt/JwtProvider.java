package com.develokit.maeum_ieum.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.ex.CustomApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.security.SignatureException;
import java.util.Date;


public class JwtProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
    public static String create(LoginUser loginUser){ //토큰 생성
        String jwtToken = JWT.create()
                .withSubject("CareGiver: jwt")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVo.EXPIRATION_TIME))
                .withClaim("id", loginUser.getUsername())
                .withClaim("role", loginUser.getCaregiver().getRole().toString())
                .sign(Algorithm.HMAC256(JwtVo.SECRET));

        return JwtVo.TOKEN_PREFIX + jwtToken;
    }

    public static LoginUser verify(String token){ //토큰 검증 -> 리턴하는 LoginUser를 강제로 시큐리티 세션에 주입

        DecodedJWT decodedJWT;
        try {
            decodedJWT = JWT.require(Algorithm.HMAC256(JwtVo.SECRET)).build().verify(token);
        } catch (TokenExpiredException e){
            logger.error("토큰이 만료되어 더 이상 유효하지 않습니다", e);
            throw new CustomApiException("토큰 기간 만료", HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED);
        } catch(SignatureVerificationException e){
            logger.error("유효하지 않은 토큰 서명입니다", e);
            throw new CustomApiException("유효하지 않은 토큰 서명", HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED);
        } catch(JWTVerificationException e){
            logger.error("JWT 검증 중 오류가 발생했습니다", e);
            throw new CustomApiException("유효하지 않은 토큰",HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED);
        }

        String username =
                decodedJWT.getClaim("id").asString();
        String role = decodedJWT.getClaim("role").asString();


        Caregiver caregiver =
                Caregiver.builder().username(username).role(Role.valueOf(role)).build();

        return new LoginUser(caregiver);
    }



}
