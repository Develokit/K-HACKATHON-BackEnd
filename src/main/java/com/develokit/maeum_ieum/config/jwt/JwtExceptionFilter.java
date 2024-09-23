package com.develokit.maeum_ieum.config.jwt;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.util.ApiUtil;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.develokit.maeum_ieum.util.api.ApiResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtExceptionFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomApiException e) {
            log.error("CustomApiException: URI = {}, 메시지 = {} ",request.getRequestURI(), e.getMessage(), e);
            setErrorResponse(e.getHttpStatus(), response, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected Exception: URI = {}, 메시지 = {}", request.getRequestURI(), e.getMessage(), e);
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, "서버 내부 오류가 발생했습니다");
        }
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, String message) throws IOException {
        ApiResult<Object> jwtExceptionResponse = ApiUtil.error(message, status.value());
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(CustomUtil.convertToJson(jwtExceptionResponse));
    }
}
