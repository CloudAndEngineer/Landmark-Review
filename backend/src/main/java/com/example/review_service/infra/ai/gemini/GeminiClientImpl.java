package com.example.review_service.infra.ai.gemini;

import com.example.review_service.global.config.GeminiConfig;
import com.example.review_service.infra.ai.gemini.dto.GeminiApiDto;
import com.example.review_service.infra.ai.prompt.PromptTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClientImpl implements GeminiClient {

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
        try {
            GeminiApiDto.Request requestPayload = GeminiApiDto.Request.of(prompt);

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

            log.warn("Gemini API 응답 결과가 비어있습니다.");
            return "요약을 생성하지 못했습니다.";

        } catch (Exception e) {
            log.error("Gemini API 호출 중 에러 발생: {}", e.getMessage(), e);
            return "AI 요약 서비스를 일시적으로 사용할 수 없습니다.";
        }
    }
}