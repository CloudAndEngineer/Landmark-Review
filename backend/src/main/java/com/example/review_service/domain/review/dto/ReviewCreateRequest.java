package com.example.review_service.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequest {

    // --- Place 정보 ---
    @NotNull(message = "장소 ID는 필수 입력값입니다.")
    private Long placeId;          // Kakao Place ID

    @NotBlank(message = "장소명은 필수 입력값입니다.")
    private String placeName;       // 장소명

    @NotBlank(message = "주소는 필수 입력값입니다.")
    private String address;         // 주소

    @NotNull(message = "위도는 필수 입력값입니다.")
    private Double latitude;        // 위도

    @NotNull(message = "경도는 필수 입력값입니다.")
    private Double longitude;       // 경도

    private String categoryName;    // 카테고리명

    // --- Review 정보 ---
    @NotNull(message = "별점은 필수 입력값입니다.")
    @Min(value = 1, message = "별점은 최소 1점 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 최대 5점 이하이어야 합니다.")
    private Integer rating;         // 별점 (1~5)

    @Size(max = 10, message = "태그는 최대 10개까지 등록 가능합니다.")
    private List<String> tags;      // 태그 목록 (예: ["경치좋은", "주차편한"])

    @NotBlank(message = "리뷰 본문은 필수 입력값입니다.")
    @Size(max = 1000, message = "리뷰 본문은 최대 1000자까지 입력 가능합니다.")
    private String content;         // 리뷰 본문
}