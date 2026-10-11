package com.example.review_service.infra.ai.prompt;

public class PromptTemplates {

    public static String buildSingleReviewPrompt(String reviewContent) {
        return """
                당신은 여행지 리뷰 요약 전문가입니다.
                다음 사용자의 여행지 후기를 읽고, 핵심 내용을 친절한 어조로 1~2문장으로 요약해 주세요.
                별도의 인삿말이나 부연 설명 없이 요약 결과 문장만 출력하세요.
                
                [리뷰 내용]
                %s
                """.formatted(reviewContent);
    }

    public static String buildOverallSummaryPrompt(String existingSummary, String newReviewContent) {
        if (existingSummary == null || existingSummary.isBlank()) {
            return """
                    당신은 여행지 분위기 분석 전문가입니다.
                    다음 신규 여행지 리뷰를 바탕으로 이 장소의 전반적인 분위기와 특징을 2~3문장으로 요약해 주세요.
                    부연 설명 없이 요약문만 출력하세요.
                    
                    [신규 리뷰]
                    %s
                    """.formatted(newReviewContent);
        }

        return """
                당신은 여행지 분위기 분석 전문가입니다.
                이전에 작성된 [기존 장소 분위기 요약]에 [신규 작성된 리뷰]의 내용을 자연스럽게 반영하여,
                이 장소의 전체적인 분위기, 장단점, 방문 팁을 종합한 최신 요약문(2~3문장)으로 업데이트해 주세요.
                부연 설명 없이 최종 요약문만 출력하세요.
                
                [기존 장소 분위기 요약]
                %s
                
                [신규 작성된 리뷰]
                %s
                """.formatted(existingSummary, newReviewContent);
    }
}