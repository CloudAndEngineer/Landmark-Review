package com.example.review_service.domain.place.service;

import com.example.review_service.domain.place.dto.PlaceDetailResponse;
import com.example.review_service.domain.place.entity.Place;
import com.example.review_service.domain.place.repository.PlaceRepository;
import com.example.review_service.domain.review.entity.Review;
import com.example.review_service.domain.review.repository.ReviewRepository;
import com.example.review_service.global.error.exception.PlaceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 장소 ID 기반 상세 정보를 조회합니다.
     * 장소 기본 정보 + DB에 캐싱된 전반적 AI 분위기 요약 + 최신 리뷰 목록 + 평균 별점을 반환합니다.
     */
    public PlaceDetailResponse getPlaceDetails(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException("해당 장소를 찾을 수 없습니다. ID: " + placeId));

        List<Review> reviews = reviewRepository.findByPlaceIdOrderByCreatedAtDesc(placeId);
        Double averageRating = reviewRepository.findAverageRatingByPlaceId(placeId);

        // 리뷰가 아직 없는 경우 평균 별점을 0.0으로 처리
        double calculatedAverage = (averageRating != null) ? averageRating : 0.0;

        return PlaceDetailResponse.of(place, reviews, calculatedAverage);
    }
}