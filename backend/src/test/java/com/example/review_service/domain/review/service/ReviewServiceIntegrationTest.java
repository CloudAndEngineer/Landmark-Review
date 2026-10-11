package com.example.review_service.domain.review.service;

import com.example.review_service.domain.place.entity.Place;
import com.example.review_service.domain.review.dto.ReviewCreateRequest;
import com.example.review_service.domain.review.dto.ReviewCreateResponse;
import com.example.review_service.domain.review.entity.Review;
import com.example.review_service.support.FixtureFactory;
import com.example.review_service.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class ReviewServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ReviewService reviewService;

    @Test
    @DisplayName("신규 장소에 첫 리뷰 작성 시 장소와 리뷰가 DB에 정상 저장되고 overallSummary가 새로 생성된다.")
    void createReview_WithNewPlace_Success() {
        // given
        Long placeId = 1001L;
        String reviewContent = "해질녘 노을이 아름답고 산책로가 아주 잘 되어 있습니다.";
        ReviewCreateRequest request = FixtureFactory.createReviewCreateRequest(placeId, reviewContent);

        // GeminiClient 모킹
        given(geminiClient.summarizeSingleReview(reviewContent))
                .willReturn("노을과 산책로가 훌륭하다는 후기입니다.");
        given(geminiClient.updateOverallSummary(null, reviewContent))
                .willReturn("해질녘 노을이 아름답고 산책로가 우수한 장소입니다.");

        // when
        ReviewCreateResponse response = reviewService.createReview(request);

        // then
        // 1. 반환 DTO 검증
        assertThat(response.getPlaceId()).isEqualTo(placeId);
        assertThat(response.getReviewSummary()).isEqualTo("노을과 산책로가 훌륭하다는 후기입니다.");

        // 2. DB Place 엔티티 저장 및 overallSummary 갱신 검증
        Place savedPlace = placeRepository.findById(placeId).orElseThrow();
        assertThat(savedPlace.getPlaceName()).isEqualTo("강릉 경포대 해수욕장");
        assertThat(savedPlace.getOverallSummary()).isEqualTo("해질녘 노을이 아름답고 산책로가 우수한 장소입니다.");

        // 3. DB Review 엔티티 저장 검증
        List<Review> reviews = reviewRepository.findByPlaceIdOrderByCreatedAtDesc(placeId);
        assertThat(reviews).hasSize(1);
        assertThat(reviews.getFirst().getContent()).isEqualTo(reviewContent);
        assertThat(reviews.getFirst().getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("기존 장소에 두 번째 리뷰 등록 시 기존 overallSummary와 합쳐져 AI 분위기 요약이 갱신된다.")
    void createReview_WithExistingPlace_UpdatesOverallSummary() {
        // given
        Long placeId = 2002L;
        Place existingPlace = Place.builder()
                .id(placeId)
                .placeName("속초 해수욕장")
                .address("강원도 속초시 조양동")
                .latitude(38.1906)
                .longitude(128.6034)
                .categoryName("해수욕장")
                .overallSummary("경관이 뛰어나고 경치가 아름다운 해변입니다.")
                .build();
        placeRepository.save(existingPlace);

        String newReviewContent = "주말에는 주차가 많이 혼잡해서 대중교통 이용을 추천합니다.";
        ReviewCreateRequest request = FixtureFactory.createReviewCreateRequest(placeId, newReviewContent);

        given(geminiClient.summarizeSingleReview(newReviewContent))
                .willReturn("주말 주차가 혼잡하다는 후기입니다.");
        given(geminiClient.updateOverallSummary(existingPlace.getOverallSummary(), newReviewContent))
                .willReturn("경관이 뛰어나고 경치가 아름답지만 주말 주차가 혼잡한 해변입니다.");

        // when
        reviewService.createReview(request);

        // then
        Place updatedPlace = placeRepository.findById(placeId).orElseThrow();
        assertThat(updatedPlace.getOverallSummary())
                .isEqualTo("경관이 뛰어나고 경치가 아름답지만 주말 주차가 혼잡한 해변입니다.");
    }
}