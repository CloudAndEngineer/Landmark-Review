package com.example.review_service.domain.review.controller;

import com.example.review_service.domain.review.dto.ReviewCreateRequest;
import com.example.review_service.domain.review.dto.ReviewCreateResponse;
import com.example.review_service.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewCreateResponse> createReview(@Valid @RequestBody ReviewCreateRequest request) {
        ReviewCreateResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}