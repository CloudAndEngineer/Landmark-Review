package com.example.review_service.domain.place.controller;

import com.example.review_service.domain.place.dto.PlaceDetailResponse;
import com.example.review_service.domain.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceDetailResponse> getPlaceDetails(@PathVariable("placeId") Long placeId) {
        PlaceDetailResponse response = placeService.getPlaceDetails(placeId);
        return ResponseEntity.ok(response);
    }
}