package com.example.review_service.domain.place.repository;

import com.example.review_service.domain.place.entity.Place;
import com.example.review_service.support.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PlaceRepositoryTest {
    @Autowired
    private PlaceRepository placeRepository;

    @Test
    @DisplayName("Kakao Place ID를 PK로 사용하여 장소를 성공적으로 저장하고 조회한다.")
    void saveAndFindById() {
        // given
        Long kakaoPlaceId = 21162441L;
        Place place = FixtureFactory.createPlace(kakaoPlaceId, "남산서울타워");

        // when
        placeRepository.save(place);
        Optional<Place> foundPlace = placeRepository.findById(kakaoPlaceId);

        // then
        assertThat(foundPlace).isPresent();
        assertThat(foundPlace.get().getId()).isEqualTo(kakaoPlaceId);
        assertThat(foundPlace.get().getPlaceName()).isEqualTo("남산서울타워");
        assertThat(foundPlace.get().getOverallSummary()).isEqualTo("방문객 평이 전반적으로 좋은 장소입니다.");
    }
}
