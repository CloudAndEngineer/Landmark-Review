package com.example.review_service.infra.ai.gemini;

import com.example.review_service.global.config.GeminiConfig;
import com.example.review_service.infra.ai.gemini.dto.GeminiApiDto;
import com.example.review_service.infra.ai.prompt.PromptTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClientImpl implements GeminiClient {

    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MILLIS = 1_000L;
    private static final long MAX_BACKOFF_MILLIS = 4_000L;

    private final RestClient geminiRestClient;
    private final GeminiConfig geminiConfig;

    @Override
    public String summarizeSingleReview(String reviewContent) {
        String prompt = PromptTemplates.buildSingleReviewPrompt(reviewContent);
        return callGeminiApi(prompt);
    }

    @Override
    public String updateOverallSummary(String existingSummary, String newReviewContent) {
        String prompt = PromptTemplates.buildOverallSummaryPrompt(existingSummary, newReviewContent);
        return callGeminiApi(prompt);
    }

    private String callGeminiApi(String prompt) {
        GeminiApiDto.Request requestPayload = GeminiApiDto.Request.of(prompt);

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                GeminiApiDto.Response response = geminiRestClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("key", geminiConfig.getApiKey())
                                .build())
                        .body(requestPayload)
                        .retrieve()
                        .body(GeminiApiDto.Response.class);

                if (response != null && response.candidates() != null && !response.candidates().isEmpty()) {
                    return response.candidates().getFirst().content().parts().getFirst().text().trim();
                }

                log.warn("Gemini API response did not contain any candidates.");
                return "요약을 생성하지 못했습니다.";
            } catch (Exception e) {
                if (attempt == MAX_ATTEMPTS || !isRetryable(e)) {
                    log.error("Gemini API call failed after {} attempt(s): {}", attempt, e.getMessage(), e);
                    return "AI 요약 서비스를 일시적으로 사용할 수 없습니다.";
                }

                long delayMillis = calculateBackoffMillis(attempt);
                log.warn("Retryable Gemini API failure on attempt {} of {}; retrying in {} ms: {}",
                        attempt, MAX_ATTEMPTS, delayMillis, e.getMessage());
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    log.warn("Interrupted while waiting to retry Gemini API call.", interrupted);
                    return "AI 요약 서비스를 일시적으로 사용할 수 없습니다.";
                }
            }
        }

        return "AI 요약 서비스를 일시적으로 사용할 수 없습니다.";
    }

    private boolean isRetryable(Exception exception) {
        if (exception instanceof ResourceAccessException) {
            return true;
        }
        if (exception instanceof RestClientResponseException responseException) {
            int status = responseException.getStatusCode().value();
            return status == 408 || status == 429 || status == 503;
        }
        return false;
    }

    private long calculateBackoffMillis(int attempt) {
        long exponentialDelay = Math.min(INITIAL_BACKOFF_MILLIS << (attempt - 1), MAX_BACKOFF_MILLIS);
        long jitter = ThreadLocalRandom.current().nextLong(exponentialDelay / 2 + 1);
        return exponentialDelay / 2 + jitter;
    }
}
