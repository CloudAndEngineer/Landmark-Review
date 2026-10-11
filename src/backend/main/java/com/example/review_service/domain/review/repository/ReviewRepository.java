package com.example.review_service.domain.review.repository;

import com.example.review_service.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    /**
     * 특정 장소의 리뷰 목록을 최신 작성일 기준 내림차순으로 조회합니다.
     */
    List<Review> findByPlaceIdOrderByCreatedAtDesc(Long placeId);

    /**
     * 특정 장소의 평균 별점을 계산합니다.
     * 등록된 리뷰가 없는 경우 null을 반환합니다.
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.place.id = :placeId")
    Double findAverageRatingByPlaceId(@Param("placeId") Long placeId);
}
