package com.example.review_service.domain.place.entity;

import com.example.review_service.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Place {
    @Id // PK
    @Column(name = "place_id")
    private Long id; // Kakao Place ID

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String categoryName;

    @Lob // Large Object
    @Column(columnDefinition = "TEXT")
    private String overallSummary;

    @Builder.Default
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public void updateOverallSummary(String newSummary) {
        this.overallSummary = newSummary;
    }
}
