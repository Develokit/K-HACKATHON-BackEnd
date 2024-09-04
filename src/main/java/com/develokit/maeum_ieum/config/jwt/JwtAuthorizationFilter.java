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

    //헤더 검증
    private boolean isHeaderVerify(HttpServletRequest request){
        try {
            HandlerMethod handlerMethod = (HandlerMethod) handlerMapping.getHandler(request).getHandler();
            if (handlerMethod.getMethodAnnotation(RequireAuth.class) != null) {
                String header = request.getHeader(JwtVo.HEADER);
                if (header == null || !header.startsWith(JwtVo.TOKEN_PREFIX)) {
                    throw new CustomApiException("Authorization 헤더 재확인 바람", HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED);
                } else return true; //헤더 검사 통과하면 필터 내에서 토큰 검증
            }
            return false;
        }catch(CustomApiException e){
            throw e;
        }catch (Exception e){
            if (request.getRequestURI().contains("swagger-ui")) //스웨거 접근 허용
                return false;
            logger.error(e.getMessage());
            throw new CustomApiException("헤더 검증 과정에서 에러 발생",HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
