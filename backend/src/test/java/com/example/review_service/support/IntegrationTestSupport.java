package com.example.review_service.support;

import com.example.review_service.domain.place.repository.PlaceRepository;
import com.example.review_service.domain.review.repository.ReviewRepository;
import com.example.review_service.infra.ai.gemini.GeminiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
        org.springframework.ai.model.google.genai.autoconfigure.chat.GoogleGenAiChatAutoConfiguration.class
})
@Transactional
public abstract class IntegrationTestSupport {

    @Autowired
    protected PlaceRepository placeRepository;

    @Autowired
    protected ReviewRepository reviewRepository;

    @MockitoBean
    protected GeminiClient geminiClient;
}