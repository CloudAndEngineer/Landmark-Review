package com.example.review_service.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GeminiConfig {
    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent}")
    private String geminiApiUrl;

    @Getter
    @Value("${gemini.api.key}")
    private String apiKey;

    @Bean
    public RestClient geminiRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(geminiApiUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
