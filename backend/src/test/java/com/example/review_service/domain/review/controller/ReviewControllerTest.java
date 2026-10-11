package com.example.review_service.domain.review.controller;

import com.example.review_service.domain.review.dto.ReviewCreateRequest;
import com.example.review_service.domain.review.dto.ReviewCreateResponse;
import com.example.review_service.support.ControllerTestSupport;
import com.example.review_service.support.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("신규 리뷰 등록 요청 시 HTTP 201 Created 응답과 생성된 리뷰 정보를 반환한다.")
    void createReview_Success() throws Exception {
        // given
        ReviewCreateRequest request = FixtureFactory.createReviewCreateRequest(1001L, "바다가 깨끗하고 멋있습니다.");

        ReviewCreateResponse response = ReviewCreateResponse.builder()
                .reviewId(1L)
                .placeId(1001L)
                .rating(5)
                .tags(List.of("바다", "힐링"))
                .content("바다가 깨끗하고 멋있습니다.")
                .reviewSummary("바다 뷰가 훌륭하다는 후기입니다.")
                .createdAt(LocalDateTime.now())
                .build();

        given(reviewService.createReview(any(ReviewCreateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/reviews")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId").value(1L))
                .andExpect(jsonPath("$.placeId").value(1001L))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.reviewSummary").value("바다 뷰가 훌륭하다는 후기입니다."));

        verify(reviewService).createReview(any(ReviewCreateRequest.class));
    }

    @Test
    @DisplayName("리뷰 생성 중 잘못된 인자 전달(IllegalArgumentException) 시 HTTP 422 Unprocessable Entity 응답을 반환한다.")
    void createReview_IllegalArgumentException_Returns422() throws Exception {
        // given
        ReviewCreateRequest request = FixtureFactory.createReviewCreateRequest(1001L, "");
        given(reviewService.createReview(any(ReviewCreateRequest.class)))
                .willThrow(new IllegalArgumentException("리뷰 본문은 필수 입력값입니다."));

        // when & then
        mockMvc.perform(post("/api/v1/reviews")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableContent()) // HTTP 422 검증
                .andExpect(jsonPath("$.message").value("리뷰 본문은 필수 입력값입니다."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("리뷰 본문이 공백인 경우 @Valid 검증에 실패하여 HTTP 422 Unprocessable Entity 응답을 반환한다.")
    void createReview_BlankContent_Returns422() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .placeId(1001L)
                .placeName("강릉 경포대")
                .address("강원도 강릉시")
                .latitude(37.805)
                .longitude(128.907)
                .rating(5)
                .content("   ") // 👈 공백 본문
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/reviews")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableContent()) // 422 검증
                .andExpect(jsonPath("$.message").value("리뷰 본문은 필수 입력값입니다."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("별점이 범위를 벗어난 경우(6점) @Valid 검증에 실패하여 HTTP 422 Unprocessable Entity 응답을 반환한다.")
    void createReview_InvalidRating_Returns422() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .placeId(1001L)
                .placeName("강릉 경포대")
                .address("강원도 강릉시")
                .latitude(37.805)
                .longitude(128.907)
                .rating(6) // 👈 범위 초과 (최대 5)
                .content("좋은 장소입니다.")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/reviews")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableContent()) // 422 검증
                .andExpect(jsonPath("$.message").value("별점은 최대 5점 이하이어야 합니다."))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}