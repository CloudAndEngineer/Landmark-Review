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
public class ReviewItemResponse {

    private Long reviewId;
    private Integer rating;
    private List<String> tags;
    private String content;
    private String reviewSummary;
    private LocalDateTime createdAt;

    public static ReviewItemResponse from(Review review) {
        return ReviewItemResponse.builder()
                .reviewId(review.getId())
                .rating(review.getRating())
                .tags(review.getTags())
                .content(review.getContent())
                .reviewSummary(review.getReviewSummary())
                .createdAt(review.getCreatedAt())
                .build();
    }
}