package com.develokit.maeum_ieum.config.jwt;
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
        try{
            filterChain.doFilter(request, response);
        }catch (CustomApiException e){
            setErrorResponse(HttpStatus.UNAUTHORIZED, request, response, e);
        }
    }
    public void setErrorResponse(HttpStatus status, HttpServletRequest request, HttpServletResponse response, Throwable throwable) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");

        ApiResult<Object> jwtExceptionResponse = ApiUtil.error(throwable.getMessage(), HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(CustomUtil.convertToJson(jwtExceptionResponse));
    }
}
