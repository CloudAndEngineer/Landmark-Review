package com.example.review_service.support;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

@WebMvcTest(controllers = {ReviewController.class, PlaceController.class})
public class ControllerTestSupport {
}
