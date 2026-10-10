package com.example.review_service.domain.review.entity;

import com.example.review_service.domain.place.entity.Place;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generates PK. IDENTITY means delegating this role to DB
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(nullable = false)
    private Integer rating; // 별점 (1~5)

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "review_tags", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>(); // 방문 유형 태그 ("데이트", "아이 동반" 등)

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 원본 리뷰 내용

    @Lob
    @Column(columnDefinition = "TEXT")
    private String reviewSummary; // 개별 리뷰 AI 요약

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
