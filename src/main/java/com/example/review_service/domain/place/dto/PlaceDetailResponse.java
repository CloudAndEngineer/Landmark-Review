package com.example.review_service.domain.place.dto;

import com.example.review_service.domain.place.entity.Place;
import com.example.review_service.domain.review.dto.ReviewItemResponse;
import com.example.review_service.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDetailResponse {

    private Long placeId;
    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String categoryName;
    private String overallSummary;          // AI가 생성 및 갱신한 장소 전체 분위기 요약문
    private Double averageRating;            // 장소 평균 별점
    private List<ReviewItemResponse> reviews; // 최신순 리뷰 목록

    public static PlaceDetailResponse of(Place place, List<Review> reviews, Double averageRating) {
        List<ReviewItemResponse> reviewResponses = reviews.stream()
                .map(ReviewItemResponse::from)
                .toList();

        return PlaceDetailResponse.builder()
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .categoryName(place.getCategoryName())
                .overallSummary(place.getOverallSummary())
                .averageRating(averageRating)
                .reviews(reviewResponses)
                .build();
    }
}