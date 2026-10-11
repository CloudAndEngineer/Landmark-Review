package com.example.review_service.infra.ai.gemini.dto;

import java.util.List;

public class GeminiApiDto {

    // --- Request ---
    public record Request(List<Content> contents) {
        public static Request of(String prompt) {
            return new Request(List.of(new Content(List.of(new Part(prompt)))));
        }
    }

    public record Content(List<Part> parts) {}
    public record Part(String text) {}

    // --- Response ---
    public record Response(List<Candidate> candidates) {}
    public record Candidate(Content content) {}
}