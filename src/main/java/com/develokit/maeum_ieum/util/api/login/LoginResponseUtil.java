package com.develokit.maeum_ieum.util.api.login;

import com.develokit.maeum_ieum.dto.loginUser.RespDto;
import com.develokit.maeum_ieum.dto.loginUser.RespDto.LoginRespDto;
import com.develokit.maeum_ieum.util.ApiUtil;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.develokit.maeum_ieum.util.api.ApiResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class LoginResponseUtil {

    private final static Logger log = LoggerFactory.getLogger(LoginResponseUtil.class);

    public static void success(HttpServletResponse response, LoginRespDto loginRespDto){

        try {
            ObjectMapper om = new ObjectMapper();
            ApiResult<LoginRespDto> responseDto = ApiUtil.success(loginRespDto);
            String responseBody = CustomUtil.convertToJson(responseDto);

            response.setContentType("application/json; charset=utf-8");
            response.setStatus(200);
            response.getWriter().println(responseBody); //만약 response status가 403이면, 그 response를 가로채고 내용을 "error"로 바꿈

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static void fail(HttpServletResponse response, String message) throws IOException {

        try {
            ObjectMapper om = new ObjectMapper();
            ApiResult<Object> responseDto = ApiUtil.error(message, HttpStatus.UNAUTHORIZED.value());
            String responseBody = CustomUtil.convertToJson(responseDto);

            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().println(responseBody);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
