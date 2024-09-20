package com.develokit.maeum_ieum.config.jwt;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomApiException e) {
            logger.error("CustomApiException: ", e);
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, e.getMessage());
        } catch (TokenExpiredException e) {
            logger.error("TokenExpiredException: ", e);
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, "토큰이 만료되었습니다");
        } catch (SignatureVerificationException e) {
            logger.error("SignatureVerificationException: ", e);
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, "유효하지 않은 토큰 서명입니다");
        } catch (JWTVerificationException e) {
            logger.error("JWTVerificationException: ", e);
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, "유효하지 않은 토큰입니다");
        } catch (Exception e) {
            logger.error("Unexpected Exception: ", e);
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, "서버 내부 오류가 발생했습니다");
        }
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");

        ApiResult<Object> jwtExceptionResponse = ApiUtil.error(message, status.value());
        response.getWriter().write(CustomUtil.convertToJson(jwtExceptionResponse));
    }
}
