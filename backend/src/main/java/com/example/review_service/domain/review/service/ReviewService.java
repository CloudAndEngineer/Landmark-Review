package com.example.review_service.domain.review.service;

import com.example.review_service.domain.place.entity.Place;
import com.example.review_service.domain.place.repository.PlaceRepository;
import com.example.review_service.domain.review.dto.ReviewCreateRequest;
import com.example.review_service.domain.review.dto.ReviewCreateResponse;
import com.example.review_service.domain.review.entity.Review;
import com.example.review_service.domain.review.repository.ReviewRepository;
import com.example.review_service.infra.ai.gemini.GeminiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final GeminiClient geminiClient;

    /**
     * 신규 리뷰를 등록합니다.
     * 1. 장소 미등록 시 Kakao Place 정보 기반으로 신규 장소 생성 및 저장
     * 2. Gemini API를 호출하여 작성된 리뷰 텍스트를 1~2문장으로 개별 요약
     * 3. 리뷰 엔티티 저장
     * 4. 기존 장소 분위기 요약(캐시) + 신규 리뷰를 Gemini에 전달하여 전반적 분위기 요약 갱신 후 DB 반영
     */
    public ReviewCreateResponse createReview(ReviewCreateRequest request) {
        // 1. 장소 조회 또는 신규 생성
        Place place = placeRepository.findById(request.getPlaceId())
                .orElseGet(() -> saveNewPlace(request));

        // 2. Gemini API를 통해 개별 리뷰 요약 획득
        String reviewSummary = geminiClient.summarizeSingleReview(request.getContent());

        // 3. Review 엔티티 생성 및 저장
        Review review = Review.builder()
                .place(place)
                .rating(request.getRating())
                .tags(request.getTags())
                .content(request.getContent())
                .reviewSummary(reviewSummary)
                .build();

        Review savedReview = reviewRepository.save(review);

        // 4. 전반적 장소 분위기 AI 요약 증분 갱신
        updateOverallPlaceSummary(place, request.getContent());

        return ReviewCreateResponse.from(savedReview);
    }

    private Place saveNewPlace(ReviewCreateRequest request) {
        Place newPlace = Place.builder()
                .id(request.getPlaceId())
                .placeName(request.getPlaceName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .categoryName(request.getCategoryName())
                .build();

        return placeRepository.save(newPlace);
    }

    private void updateOverallPlaceSummary(Place place, String newReviewContent) {
        String currentSummary = place.getOverallSummary();

        // 기존 요약 + 신규 리뷰를 전달하여 최신 전체 분위기 요약문으로 갱신
        String updatedOverallSummary = geminiClient.updateOverallSummary(currentSummary, newReviewContent);

        // Place 도메인 객체의 요약문 업데이트 (JPA Dirty Checking에 의해 자동 저장)
        place.updateOverallSummary(updatedOverallSummary);
    }
}