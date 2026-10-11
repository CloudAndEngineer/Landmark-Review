package com.example.review_service.infra.ai.gemini;

public interface GeminiClient {
    /**
     * 단일 작성 리뷰 텍스트를 1~2문장으로 요약합니다.
     */
    String summarizeSingleReview(String reviewContent);

    /**
     * 기존 장소 분위기 요약과 신규 작성된 리뷰를 합성하여 최신 장소 분위기 요약으로 갱신합니다.
     */
    String updateOverallSummary(String existingSummary, String newReviewContent);
}
