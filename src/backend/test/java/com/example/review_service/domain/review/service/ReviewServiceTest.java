package com.example.review_service.domain.review.service;

import com.example.review_service.domain.place.entity.Place;
import com.example.review_service.domain.place.repository.PlaceRepository;
import com.example.review_service.domain.review.dto.ReviewCreateRequest;
import com.example.review_service.domain.review.dto.ReviewCreateResponse;
import com.example.review_service.domain.review.entity.Review;
import com.example.review_service.domain.review.repository.ReviewRepository;
import com.example.review_service.infra.ai.gemini.GeminiClient;
import com.example.review_service.support.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private GeminiClient geminiClient;

    @Test
    @DisplayName("기존 장소에 리뷰를 작성하면 개별 AI 요약과 전반적 분위기 AI 요약이 모두 수행된다.")
    void createReviewSuccessExistingPlace() {
        // given
        Long placeId = 1001L;
        Place place = FixtureFactory.createPlace(placeId, "강릉 경포대");
        ReviewCreateRequest request = FixtureFactory.createReviewCreateRequest(placeId, "경포대 바다 뷰가 멋집니다.");

        given(placeRepository.findById(placeId)).willReturn(Optional.of(place));
        given(geminiClient.summarizeSingleReview(request.getContent()))
                .willReturn("바다 뷰가 멋지다는 후기입니다.");
        given(geminiClient.updateOverallSummary(place.getOverallSummary(), request.getContent()))
                .willReturn("경관이 훌륭하며 방문객 만족도가 높은 장소입니다.");
        given(reviewRepository.save(any(Review.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ReviewCreateResponse response = reviewService.createReview(request);

        // then
        assertThat(response.getPlaceId()).isEqualTo(placeId);
        assertThat(response.getReviewSummary()).isEqualTo("바다 뷰가 멋지다는 후기입니다.");

        // 장소 분위기 요약 캐시가 최신으로 갱신되었는지 검증
        assertThat(place.getOverallSummary()).isEqualTo("경관이 훌륭하며 방문객 만족도가 높은 장소입니다.");

        verify(geminiClient).summarizeSingleReview(request.getContent());
        verify(geminiClient).updateOverallSummary(any(), any());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("등록되지 않은 신규 장소에 리뷰 작성 시 장소를 먼저 저장한 후 리뷰를 등록한다.")
    void createReview_Success_NewPlace() {
        // given
        Long newPlaceId = 2002L;
        ReviewCreateRequest request = FixtureFactory.createReviewCreateRequest(newPlaceId, "새로운 장소 후기입니다.");

        given(placeRepository.findById(newPlaceId)).willReturn(Optional.empty());
        given(placeRepository.save(any(Place.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(geminiClient.summarizeSingleReview(any())).willReturn("개별 요약");
        given(geminiClient.updateOverallSummary(any(), any())).willReturn("초기 분위기 요약");
        given(reviewRepository.save(any(Review.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ReviewCreateResponse response = reviewService.createReview(request);

        // then
        assertThat(response.getPlaceId()).isEqualTo(newPlaceId);
        verify(placeRepository).save(any(Place.class)); // 신규 장소 저장 호출 확인
        verify(reviewRepository).save(any(Review.class));
    }
}
