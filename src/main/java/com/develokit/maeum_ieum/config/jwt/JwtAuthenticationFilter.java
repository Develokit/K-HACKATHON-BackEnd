package com.develokit.maeum_ieum.config.jwt;

import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.dto.loginUser.ReqDto;
import com.develokit.maeum_ieum.dto.loginUser.RespDto;
import com.develokit.maeum_ieum.dto.loginUser.RespDto.LoginRespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.util.api.login.LoginResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static com.develokit.maeum_ieum.dto.loginUser.ReqDto.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Logger log = LoggerFactory.getLogger((getClass()));

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager){
        super(authenticationManager);
        setFilterProcessesUrl("/api/login");
        this.authenticationManager = authenticationManager;
    }

    @Override // [POST] /api/login 일 때 동작
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper om = new ObjectMapper();
        try {
            LoginReqDto loginReqDto = om.readValue(request.getInputStream(), LoginReqDto.class);
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(loginReqDto.getUsername(), loginReqDto.getPassword());

            //시큐리티 세션
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);

            return authenticate;
        } catch (IOException e) {
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.debug("로그인 실패");
        LoginResponseUtil.fail(response, "로그인 실패");
    }

    //return authentication이 잘 작동하면 successfulAuthentication가 호출됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.debug("로그인 성공");

        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String jwtToken = JwtProvider.create(loginUser);
        response.addHeader(JwtVo.HEADER, jwtToken);

        LoginRespDto loginRespDto = new LoginRespDto(loginUser);

        LoginResponseUtil.success(response, loginRespDto);
    }
}
