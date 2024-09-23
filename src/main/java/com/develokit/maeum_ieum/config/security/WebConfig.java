package com.develokit.maeum_ieum.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Configuration
public class WebConfig {
    @Bean
    public DispatcherHandler webHandler() {
        return new DispatcherHandler();
    }

    @Bean
    public RouterFunction<ServerResponse> reactiveRoutes(DispatcherHandler dispatcherHandler) {
        return RouterFunctions.route()
                .path("/reactive", builder -> builder
                        .nest(RequestPredicates.path("/**"), nestedBuilder -> nestedBuilder
                                .GET("/**", request -> handleRequest(request, dispatcherHandler))
                                .POST("/**", request -> handleRequest(request, dispatcherHandler))
                                .PUT("/**", request -> handleRequest(request, dispatcherHandler))
                                .DELETE("/**", request -> handleRequest(request, dispatcherHandler))
                        )
                )
                .build();
    }

    private Mono<ServerResponse> handleRequest(ServerRequest request, DispatcherHandler dispatcherHandler) {
        return Mono.defer(() -> {
            // 현재 request의 exchange 가져오기
            ServerWebExchange exchange = request.exchange();

            return dispatcherHandler.handle(exchange)
                    .then(ServerResponse.ok().build())
                    .onErrorResume(e -> {
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage());
                    });
        });
    }
}
