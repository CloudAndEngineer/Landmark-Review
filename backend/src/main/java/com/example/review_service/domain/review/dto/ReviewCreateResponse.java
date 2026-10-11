package com.example.review_service.domain.review.dto;

import com.example.review_service.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateResponse {

    private Long reviewId;
    private Long placeId;
    private Integer rating;
    private List<String> tags;
    private String content;
    private String reviewSummary; // Gemini AI가 요약한 1~2문장 요약
    private LocalDateTime createdAt;

    public static ReviewCreateResponse from(Review review) {
        return ReviewCreateResponse.builder()
                .reviewId(review.getId())
                .placeId(review.getPlace().getId())
                .rating(review.getRating())
                .tags(review.getTags())
                .content(review.getContent())
                .reviewSummary(review.getReviewSummary())
                .createdAt(review.getCreatedAt())
                .build();
    }
}