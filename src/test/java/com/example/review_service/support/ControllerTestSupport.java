package com.example.review_service.support;

import com.example.review_service.domain.place.controller.PlaceController;
import com.example.review_service.domain.place.service.PlaceService;
import com.example.review_service.domain.review.controller.ReviewController;
import com.example.review_service.domain.review.service.ReviewService;
import com.example.review_service.global.error.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = {ReviewController.class, PlaceController.class, GlobalExceptionHandler.class})
public class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected ReviewService reviewService;

    @MockitoBean
    protected PlaceService placeService;
}
