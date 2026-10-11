package com.example.review_service.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequest {

    // --- Place 정보 ---
    private Long placeId;          // Kakao Place ID
    private String placeName;       // 장소명
    private String address;         // 주소
    private Double latitude;        // 위도
    private Double longitude;       // 경도
    private String categoryName;    // 카테고리명

    // --- Review 정보 ---
    private Integer rating;         // 별점 (1~5)
    private List<String> tags;      // 태그 목록 (예: ["경치좋은", "주차편한"])
    private String content;         // 리뷰 본문
}