package com.capstone.pickIt.api.course.exception;

import com.capstone.pickIt.domain.course.exception.ProfileException;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ProfileExceptionHandler {

    @ExceptionHandler(ProfileException.class)
    public ResponseEntity<ApiResponse<Void>> handleProfileException(ProfileException e) {
        log.error("ProfileException 발생: {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(ApiResponse.onFailure(e.getErrorCode(), null));
    }
}
