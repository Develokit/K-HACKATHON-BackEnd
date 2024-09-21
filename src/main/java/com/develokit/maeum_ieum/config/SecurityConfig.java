package com.develokit.maeum_ieum.config;

import com.develokit.maeum_ieum.config.jwt.JwtAuthenticationFilter;
import com.develokit.maeum_ieum.config.jwt.JwtAuthorizationFilter;
import com.develokit.maeum_ieum.config.jwt.JwtExceptionFilter;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.util.ApiUtil;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.develokit.maeum_ieum.util.api.ApiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.security.SecureRandom;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final Logger log = LoggerFactory.getLogger((getClass()));
    private final RequestMappingHandlerMapping handlerMapping;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        log.debug("디버그 : BCryptPasswordEncoder 빈 등록됨");
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecureRandom secureRandom(){
        return new SecureRandom();
    }
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity>{

        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilterBefore(jwtExceptionFilter, JwtAuthorizationFilter.class); //토큰 검증 전에 에러 잡기 위한 필터
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager)); //jwt 인증 필터
            builder.addFilter(new JwtAuthorizationFilter(authenticationManager, handlerMapping)); //인가 필터
            super.configure(builder);
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.debug("디버그 : filterChain 빈 등록됨"); //설정이 잘 되었나 테스트
        http
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(configurationSource()))
                .with(new CustomSecurityFilterManager(), CustomSecurityFilterManager::getClass)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/caregivers").permitAll()
                        .requestMatchers("/caregivers/check-username/**").permitAll()
                        .requestMatchers("/caregivers/**").authenticated()
                        .requestMatchers("/error").permitAll()  // 오류 페이지 접근 허용
                        .anyRequest().permitAll()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) //토큰 쓸 거라서 해제
                )
                .exceptionHandling(handler -> {
                    handler
                    .authenticationEntryPoint((request, response, authException) -> {
                        ObjectMapper om = new ObjectMapper();
                        ApiResult<?> responseDto = ApiUtil.error("인증되지 않은 접근입니다", HttpStatus.UNAUTHORIZED.value());
                        String responseBody = CustomUtil.convertToJson(responseDto);

                        response.setContentType("application/json; charset=utf-8");
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().println(responseBody);
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        ObjectMapper om = new ObjectMapper();
                        ApiResult<?> responseDto = ApiUtil.error("로그인을 진행해주세요", HttpStatus.FORBIDDEN.value());
                        String responseBody = CustomUtil.convertToJson(responseDto);

                        response.setContentType("application/json; charset=utf-8");
                        response.setStatus(403);
                        response.getWriter().println(responseBody); //만약 response status가 403이면, 그 response를 가로채고 내용을 "error"로 바꿈
                    });
                })
        ;


        return http.build();
    }

    public CorsConfigurationSource configurationSource() {

        log.debug("디버그 : configurationSource cors 설정이 SecurityFilterChain에 등록됨");
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedMethods(Collections.singletonList("*")); //모든 HTTP 메서드(자바스크립트 요청) 허용
        config.setAllowedHeaders(Collections.singletonList("*")); // 모든 HTTP 헤더 허용
        config.setAllowCredentials(true); //클라이언트에서 쿠키 요청 허용
        config.addAllowedOriginPattern("*"); //모든 IP 주소 허용 (프론트엔드 IP만 허용)
        config.setMaxAge(3600L); //1시간 동안 캐시
        config.addExposedHeader("Authorization"); //자바스크립트가 브라우저에서 토큰을 가져오기 위해 붙여줘야 함

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
