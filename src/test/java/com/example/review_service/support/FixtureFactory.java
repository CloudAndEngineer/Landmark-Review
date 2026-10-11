package com.example.review_service.support;

import com.example.review_service.domain.place.entity.Place;
import com.example.review_service.domain.review.dto.ReviewCreateRequest;
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

    /**
     * 기본 테스트용 ReviewCreateRequest 생성 (placeId: 1001L)
     */
    public static ReviewCreateRequest createReviewCreateRequest() {
        return createReviewCreateRequest(1001L, "경치가 정말 멋지고 바다 뷰가 아름다운 장소입니다.");
    }

    /**
     * 특정 placeId와 리뷰 본문(content)을 지정하여 ReviewCreateRequest 생성
     */
    public static ReviewCreateRequest createReviewCreateRequest(Long placeId, String content) {
        return ReviewCreateRequest.builder()
                .placeId(placeId)
                .placeName("강릉 경포대 해수욕장")
                .address("강원특별자치도 강릉시 안현동 산1-1")
                .latitude(37.805401)
                .longitude(128.907931)
                .categoryName("여행 > 명소 > 해수욕장")
                .rating(5)
                .tags(List.of("바다", "힐링", "오션뷰"))
                .content(content)
                .build();
    }
}
