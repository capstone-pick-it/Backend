package com.capstone.pickIt.api.trait.exception;

import com.capstone.pickIt.domain.trait.exception.TraitException;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TraitExceptionHandler {

    @ExceptionHandler(TraitException.class)
    public ResponseEntity<ApiResponse<Void>> handleTraitException(TraitException e) {
        log.error("TraitException 발생: {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(ApiResponse.onFailure(e.getErrorCode(), null));
    }
}