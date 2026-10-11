package com.example.review_service.domain.place.controller;

import com.example.review_service.domain.place.dto.PlaceDetailResponse;
import com.example.review_service.domain.review.dto.ReviewItemResponse;
import com.example.review_service.global.error.exception.PlaceNotFoundException;
import com.example.review_service.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlaceControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("장소 ID로 상세 정보를 조회하면 장소 기본 정보, AI 분위기 요약 및 리뷰 목록을 반환한다.")
    void getPlaceDetails_Success() throws Exception {
        // given
        Long placeId = 1001L;

        ReviewItemResponse reviewItem = ReviewItemResponse.builder()
                .reviewId(10L)
                .rating(5)
                .tags(List.of("힐링"))
                .content("경치가 아주 좋습니다.")
                .reviewSummary("경치가 훌륭하다는 평입니다.")
                .createdAt(LocalDateTime.now())
                .build();

        PlaceDetailResponse response = PlaceDetailResponse.builder()
                .placeId(placeId)
                .placeName("속초 해수욕장")
                .address("강원도 속초시 조양동")
                .latitude(38.1906)
                .longitude(128.6034)
                .categoryName("해수욕장")
                .overallSummary("전반적으로 경관이 좋고 방문객 만족도가 높은 해변입니다.")
                .averageRating(4.5)
                .reviews(List.of(reviewItem))
                .build();

        given(placeService.getPlaceDetails(placeId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/places/{placeId}", placeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placeId").value(placeId))
                .andExpect(jsonPath("$.placeName").value("속초 해수욕장"))
                .andExpect(jsonPath("$.overallSummary").value("전반적으로 경관이 좋고 방문객 만족도가 높은 해변입니다."))
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.reviews[0].reviewId").value(10L));

        verify(placeService).getPlaceDetails(placeId);
    }

    @Test
    @DisplayName("존재하지 않는 장소 ID 조회 시 PlaceNotFoundException 예외 발생에 따라 404 응답을 전달받는다.")
    void getPlaceDetails_NotFound() throws Exception {
        // given
        Long invalidPlaceId = 9999L;
        given(placeService.getPlaceDetails(invalidPlaceId))
                .willThrow(new PlaceNotFoundException("해당 장소를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/api/v1/places/{placeId}", invalidPlaceId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("장소 ID 경로 변수 타입이 숫자가 아닌 경우(Type Mismatch) HTTP 400 Bad Request 응답을 반환한다.")
    void getPlaceDetails_TypeMismatch_Returns400() throws Exception {
        // given
        String invalidStringId = "abc";

        // when & then
        mockMvc.perform(get("/api/v1/places/{placeId}", invalidStringId))
                .andDo(print())
                .andExpect(status().isBadRequest()) // HTTP 400 검증
                .andExpect(jsonPath("$.message").value("요청 파라미터 타입이 올바르지 않습니다: placeId"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}