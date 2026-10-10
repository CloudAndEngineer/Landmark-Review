package com.example.review_service.domain.review.entity;

import com.example.review_service.domain.place.entity.Place;
import com.example.review_service.support.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReviewTest {
    @Test
    @DisplayName("Review 객체를 생성할 때 별점, 태그, 본문 및 AI 요약 정보가 정상적으로 저장된다.")
    void createReview() {
        // given
        Place place = FixtureFactory.createPlace(1001L, "남산서울타워");

        // when
        Review review = Review.builder()
                .place(place)
                .rating(5)
                .tags(List.of("데이트", "야경맛집"))
                .content("야경이 정말 멋지고 야외 테라스 분위기가 너무 좋습니다.")
                .reviewSummary("야경과 테라스 분위기가 훌륭하다는 평입니다.")
                .build();

        // then
        assertThat(review.getPlace().getPlaceName()).isEqualTo("남산서울타워");
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getTags()).containsExactly("데이트", "야경맛집");
        assertThat(review.getContent()).contains("야경이 정말 멋지고");
        assertThat(review.getReviewSummary()).isEqualTo("야경과 테라스 분위기가 훌륭하다는 평입니다.");
    }

    @Test
    @DisplayName("FixtureFactory를 이용해 생성한 Review와 Place의 연관관계가 올바르게 매핑된다.")
    void createReviewWithFixture() {
        // given
        Place place = FixtureFactory.createPlace(1002L, "속초 해수욕장");

        // when
        Review review = FixtureFactory.createReview(place, 4, "모래사장이 깨끗하고 접근성이 좋습니다.");

        // then
        assertThat(review.getPlace().getId()).isEqualTo(1002L);
        assertThat(review.getRating()).isEqualTo(4);
        assertThat(review.getContent()).isEqualTo("모래사장이 깨끗하고 접근성이 좋습니다.");
    }
}
