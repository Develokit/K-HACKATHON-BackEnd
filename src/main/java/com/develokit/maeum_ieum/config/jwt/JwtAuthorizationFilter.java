package com.develokit.maeum_ieum.config.jwt;

import com.auth0.jwt.exceptions.SignatureGenerationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.ex.CustomApiException;
import io.netty.util.concurrent.BlockingOperationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SignatureException;


public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final static Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    //헤더 검증
    private boolean isHeaderVerify(HttpServletRequest request){
        String header = request.getHeader(JwtVo.HEADER);
        if(header == null || !header.startsWith(JwtVo.TOKEN_PREFIX)) return false;
        else return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException{
        if(isHeaderVerify(request)){
            //토큰 있음 -> 임시 세션 생성
            String token = request.getHeader(JwtVo.HEADER).replace(JwtVo.TOKEN_PREFIX, "");//Bearer 제거
            logger.debug("디버그 : 토큰이 존재함");


            //토큰 검증
            LoginUser loginUser = JwtProvider.verify(token);

            loginUser.getAuthorities().stream().forEach(grantedAuthority -> System.out.println("grantedAuthority.getAuthority().toString() = " + grantedAuthority.getAuthority().toString()));
            //임시 세션
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

            logger.debug("임시 세션 생성");

            //로그인
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        //다시 체인 타기
        chain.doFilter(request, response);
    }
}
