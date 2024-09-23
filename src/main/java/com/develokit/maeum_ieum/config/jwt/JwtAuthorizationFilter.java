package com.develokit.maeum_ieum.config.jwt;

import com.auth0.jwt.exceptions.*;
import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.util.ApiUtil;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.develokit.maeum_ieum.util.api.ApiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.concurrent.BlockingOperationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.security.SignatureException;


public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final RequestMappingHandlerMapping handlerMapping;

    private final static Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, RequestMappingHandlerMapping handlerMapping) {
        super(authenticationManager);
        this.handlerMapping = handlerMapping;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

            String header = request.getHeader(JwtVo.HEADER);
            if (header == null || !header.startsWith(JwtVo.TOKEN_PREFIX)) {
                chain.doFilter(request, response);
                return;
            }

            try {
                String token = header.replace(JwtVo.TOKEN_PREFIX, "");
                logger.debug("디버그 : JWT 토큰 확인됨. URI: {}", request.getRequestURI());

                // 토큰 검증
                LoginUser loginUser = JwtProvider.verify(token);

                loginUser.getAuthorities().forEach(grantedAuthority ->
                        logger.debug("디버그 : 부여된 권한: {}", grantedAuthority.getAuthority()));

                // 임시 세션 생성
                Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

                logger.debug("인증 정보 설정 완료. 사용자: {}", loginUser.getUsername());

                // 로그인
                SecurityContextHolder.getContext().setAuthentication(authentication);

                chain.doFilter(request, response);
            } catch (TokenExpiredException e) {
                setErrorResponse(HttpStatus.UNAUTHORIZED, response,  "토큰이 만료되었습니다");
            } catch (JWTDecodeException e) {
                setErrorResponse(HttpStatus.UNAUTHORIZED, response, "유효하지 않은 JWT 형식입니다");
            } catch (SignatureVerificationException e) {
                setErrorResponse(HttpStatus.UNAUTHORIZED, response, "유효하지 않은 토큰 서명입니다");
            } catch (JWTVerificationException e) {
                setErrorResponse(HttpStatus.UNAUTHORIZED, response, "유효하지 않은 토큰입니다");
            } catch (Exception e) {
                logger.error("예기치 않은 오류 발생: {}", e.getMessage(), e);
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
