package com.example.review_service.domain.place.entity;

import com.example.review_service.support.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlaceTest {
    @Test
    @DisplayName("Place 객체를 생성할 때 전달된 장소 정보가 올바르게 설정된다.")
    void createPlace() {
        // Given
        Place place = FixtureFactory.createPlace(1001L, "강릉 경포대");

        // then
        assertThat(place.getId()).isEqualTo(1001L);
        assertThat(place.getPlaceName()).isEqualTo("강릉 경포대");
        assertThat(place.getLatitude()).isEqualTo(37.5665);
        assertThat(place.getLongitude()).isEqualTo(126.9780);
    }

    @Test
    @DisplayName("전반적 리뷰 분위기 AI 요약 택스트를 최신 내용으로 갱신한다.")
    void updateOverallSummary() {
        // given
        Place place = FixtureFactory.createPlace(1001L, "강릉 경포대");
        String newSummary = "바다가 가깝고 산책하기 좋으나 주말에는 주차가 혼잡함.";

        // when
        place.updateOverallSummary(newSummary);

        // then
        assertThat(place.getOverallSummary()).isEqualTo(newSummary);
    }
}
