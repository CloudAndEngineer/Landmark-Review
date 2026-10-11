package com.example.review_service.domain.review.repository;

import com.example.review_service.domain.place.entity.Place;
import com.example.review_service.domain.place.repository.PlaceRepository;
import com.example.review_service.domain.review.entity.Review;
import com.example.review_service.support.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    @DisplayName("특정 장소의 리뷰를 작성일 기준 내림차순(최신순)으로 조회한다.")
    void findByPlaceIdOrderByCreatedAtDesc() throws InterruptedException {
        // given
        Place place = placeRepository.save(FixtureFactory.createPlace(1001L, "속초 해수욕장"));

        Review review1 = FixtureFactory.createReview(place, 5, "첫 번째 리뷰입니다.");
        reviewRepository.save(review1);

        Thread.sleep(10); // 생성 시간 차이를 위해 잠시 대기

        Review review2 = FixtureFactory.createReview(place, 3, "두 번째 리뷰입니다.");
        reviewRepository.save(review2);

        // when
        List<Review> reviews = reviewRepository.findByPlaceIdOrderByCreatedAtDesc(place.getId());

        // then
        assertThat(reviews).hasSize(2);
        assertThat(reviews.get(0).getContent()).isEqualTo("두 번째 리뷰입니다."); // 최신 등록건
        assertThat(reviews.get(1).getContent()).isEqualTo("첫 번째 리뷰입니다.");
    }

    @Test
    @DisplayName("특정 장소에 등록된 리뷰들의 평균 별점을 올바르게 계산한다.")
    void findAverageRatingByPlaceId() {
        // given
        Place place = placeRepository.save(FixtureFactory.createPlace(1002L, "경포대"));

        reviewRepository.save(FixtureFactory.createReview(place, 5, "최고예요"));
        reviewRepository.save(FixtureFactory.createReview(place, 4, "좋아요"));
        reviewRepository.save(FixtureFactory.createReview(place, 3, "보통이에요"));

        // when
        Double averageRating = reviewRepository.findAverageRatingByPlaceId(place.getId());

        // then
        assertThat(averageRating).isEqualTo(4.0);
    }

    @Test
    @DisplayName("리뷰가 없는 장소의 평균 별점을 조회하면 null을 반환한다.")
    void findAverageRatingByPlaceId_NoReviews() {
        // given
        Place place = placeRepository.save(FixtureFactory.createPlace(1003L, "리뷰없는장소"));

        // when
        Double averageRating = reviewRepository.findAverageRatingByPlaceId(place.getId());

        // then
        assertThat(averageRating).isNull();
    }
}
