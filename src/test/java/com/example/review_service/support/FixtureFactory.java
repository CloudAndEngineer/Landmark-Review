package com.example.review_service.support;

import com.example.review_service.domain.place.entity.Place;
import com.example.review_service.domain.review.entity.Review;

import java.util.List;

public class FixtureFactory {
    public static Place createPlace(Long id, String name) {
        return Place.builder()
                .id(id != null ? id : 1001L)
                .placeName(name)
                .address("서울특별시 종로구 세종대로 1")
                .latitude(37.5665)
                .longitude(126.9780)
                .categoryName("관광명소")
                .overallSummary("방문객 평이 전반적으로 좋은 장소입니다.")
                .build();
    }

    public static Review createReview(Place place, Integer rating, String content) {
        return Review.builder()
                .place(place)
                .rating(rating)
                .tags(List.of("데이트", "뷰맛집"))
                .content(content)
                .reviewSummary("경치가 훌륭하다는 후기입니다.")
                .build();
    }
}
