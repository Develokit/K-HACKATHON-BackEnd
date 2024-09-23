package com.develokit.maeum_ieum.config.security;

import com.develokit.maeum_ieum.config.jwt.JwtProvider;
import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.server.WebFilter;

@Configuration
@EnableWebFluxSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE) //WebFlux 환경에서 실행될 때만 이 설정이 활성화
@Order(1)  // 이 설정이 기존 MVC 보안 설정보다 먼저 적용되도록
public class WebFluxSecurityConfig {

    private final static Logger log = LoggerFactory.getLogger(WebFluxSecurityConfig.class);
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/reactive/**"))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/reactive/**").authenticated()
                        .anyExchange().authenticated()
                )
                .addFilterBefore(webFluxJwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHORIZATION) // 필터 등록
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    public WebFilter webFluxJwtAuthenticationFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();
            log.debug("WebFlux Filter: 처리 경로: {}", path); // 추가 로그

            if (path.contains("/assistants/rules/autocomplete")) {
                String token = exchange.getRequest().getHeaders().getFirst("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    try {
                        LoginUser loginUser = JwtProvider.verify(token);
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(
                                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities())
                                ));
                    } catch (Exception e) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                }
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        };
    }
}
