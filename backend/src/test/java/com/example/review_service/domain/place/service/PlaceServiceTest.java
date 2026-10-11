package com.example.review_service.domain.place.service;

import com.example.review_service.domain.place.dto.PlaceDetailResponse;
import com.example.review_service.domain.place.entity.Place;
import com.example.review_service.domain.place.repository.PlaceRepository;
import com.example.review_service.domain.review.entity.Review;
import com.example.review_service.domain.review.repository.ReviewRepository;
import com.example.review_service.global.error.exception.PlaceNotFoundException;
import com.example.review_service.support.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PlaceServiceTest {
    @InjectMocks
    private PlaceService placeService;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("장소 ID로 상세 조회 시 장소 정보, 평균 별점, 리뷰 목록을 묶어서 반환한다.")
    void getPlaceDetails_Success() {
        // given
        Long placeId = 1001L;
        Place place = FixtureFactory.createPlace(placeId, "속초 해수욕장");
        Review review1 = FixtureFactory.createReview(place, 5, "바다가 깨끗해요.");
        Review review2 = FixtureFactory.createReview(place, 3, "사람이 너무 많아요.");

        given(placeRepository.findById(placeId)).willReturn(Optional.of(place));
        given(reviewRepository.findByPlaceIdOrderByCreatedAtDesc(placeId))
                .willReturn(List.of(review1, review2));
        given(reviewRepository.findAverageRatingByPlaceId(placeId)).willReturn(4.0);

        // when
        PlaceDetailResponse response = placeService.getPlaceDetails(placeId);

        // then
        assertThat(response.getPlaceId()).isEqualTo(placeId);
        assertThat(response.getPlaceName()).isEqualTo("속초 해수욕장");
        assertThat(response.getAverageRating()).isEqualTo(4.0);
        assertThat(response.getReviews()).hasSize(2);
        assertThat(response.getReviews().get(0).getContent()).isEqualTo("바다가 깨끗해요.");
    }

    @Test
    @DisplayName("존재하지 않는 장소 ID로 조회 시 PlaceNotFoundException이 발생한다.")
    void getPlaceDetails_NotFound() {
        // given
        Long invalidPlaceId = 9999L;
        given(placeRepository.findById(invalidPlaceId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> placeService.getPlaceDetails(invalidPlaceId))
                .isInstanceOf(PlaceNotFoundException.class);
    }
}
