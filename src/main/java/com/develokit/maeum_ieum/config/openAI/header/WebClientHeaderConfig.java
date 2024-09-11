package com.develokit.maeum_ieum.config.openAI.header;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientHeaderConfig {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${openai.key}")
    private String OPENAI_API_KEY;
    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(AUTHORIZATION_HEADER,"Bearer "+OPENAI_API_KEY)
                .defaultHeader("OpenAI-Beta", "assistants=v2")
                .exchangeStrategies(ExchangeStrategies.builder().codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }
}
