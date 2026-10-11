package com.example.review_service.global.error;

import com.example.review_service.global.error.exception.PlaceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 1. [HTTP 400 Bad Request] 문법 오류 및 타입 불일치
     * 예: /api/v1/places/abc (Long 타입에 문자열 전달 시)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("요청 파라미터 타입이 올바르지 않습니다: " + e.getName()));
    }

    /**
     * 2. [HTTP 422 Unprocessable Entity] @Valid 검증 실패 / 비즈니스 규칙 위반
     * 예: 별점이 1~5 범위를 벗어나거나, 리뷰 본문이 빈 값인 경우
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("MethodArgumentNotValidException (Validation Failed): {}", errorMessage);
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT) // 422 적용
                .body(ErrorResponse.of(errorMessage));
    }

    /**
     * 3. [HTTP 422 Unprocessable Entity] IllegalArgumentException (도메인 값 유효성 오류)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT) // 422 적용
                .body(ErrorResponse.of(e.getMessage()));
    }

    /**
     * 4. [HTTP 404 Not Found] 리소스 미존재
     */
    @ExceptionHandler(PlaceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlaceNotFoundException(PlaceNotFoundException e) {
        log.warn("PlaceNotFoundException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(e.getMessage()));
    }

    /**
     * 5. 기타 서버 내부 예상치 못한 에러 처리
     * [HTTP 500 INTERNAL_SERVER_ERROR]
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        log.error("Unhandled Exception: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("서버 내부 에러가 발생했습니다. 잠시 후 다시 시도해 주세요."));
    }
}