package com.example.review_service.infra.ai.gemini;

import com.example.review_service.global.config.GeminiConfig;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(GeminiClientImpl.class)
@Import(GeminiConfig.class)
class GeminiClientTest {

    @Autowired
    private GeminiClient geminiClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String expectedUrl;

    @BeforeEach
    void setUp() {
        // 테스트 실행 전 MockRestServiceServer 상태 초기화
        mockServer.reset();
    }

    @Test
    @DisplayName("단일 리뷰 요약 요청 시 Gemini API 규격에 맞는 HTTP 요청을 보내고 요약 결과를 파싱한다.")
    void summarizeSingleReview_Success() {
        // given
        String reviewContent = "경치가 정말 멋지고 해질녘 노을이 아름답습니다.";

        // Gemini API 가짜 응답 JSON (candidates -> content -> parts -> text)
        String mockResponseBody = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "경치와 노을이 아름답다는 평입니다."
                          }
                        ]
                      }
                    }
                  ]
                }
                """;

        // HTTP Mock 서버 기대 행동 정의 (POST 요청, URL/Query Parameter 검증)
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith(expectedUrl)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(queryParam("key", org.hamcrest.Matchers.any(String.class)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.contents[0].parts[0].text").exists())
                .andRespond(withSuccess(mockResponseBody, MediaType.APPLICATION_JSON));

        // when
        String result = geminiClient.summarizeSingleReview(reviewContent);

        // then
        assertThat(result).isEqualTo("경치와 노을이 아름답다는 평입니다.");
        mockServer.verify(); // 지정한 HTTP 요청 조건이 모두 만족되었는지 검증
    }

    @Test
    @DisplayName("전반적 분위기 요약 업데이트 시 기존 요약과 신규 리뷰가 포함된 프롬프트 요청을 보낸다.")
    void updateOverallSummary_Success() {
        // given
        String existingSummary = "조용하고 풍경이 우수한 장소입니다.";
        String newReviewContent = "주말에는 주차가 조금 혼잡해요.";

        String mockResponseBody = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "조용하고 풍경이 우수하지만 주말 주차가 혼잡한 편입니다."
                          }
                        ]
                      }
                    }
                  ]
                }
                """;

        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith(expectedUrl)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(mockResponseBody, MediaType.APPLICATION_JSON));

        // when
        String updatedSummary = geminiClient.updateOverallSummary(existingSummary, newReviewContent);

        // then
        assertThat(updatedSummary).isEqualTo("조용하고 풍경이 우수하지만 주말 주차가 혼잡한 편입니다.");
        mockServer.verify();
    }

    @Test
    @DisplayName("Gemini API 호출 중 500 에러 발생 시 예외를 캡처하고 대체 텍스트(Fallback)를 반환한다.")
    void summarizeSingleReview_ServerError_Fallback() {
        // given
        String reviewContent = "테스트 리뷰입니다.";

        // HTTP 500 Internal Server Error 응답 모킹
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith(expectedUrl)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        // when
        String result = geminiClient.summarizeSingleReview(reviewContent);

        // then
        assertThat(result).isEqualTo("AI 요약 서비스를 일시적으로 사용할 수 없습니다.");
        mockServer.verify();
    }
}