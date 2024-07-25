package com.develokit.maeum_ieum.config.openAI.header;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class OpenAiHeaderConfiguration {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${openai.key}")
    private String OPENAI_API_KEY;

    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                requestTemplate.header(AUTHORIZATION_HEADER, "Bearer "+OPENAI_API_KEY);
                requestTemplate.header("OpenAI-Beta", "assistants=v2");
            }
        };
    }


}
