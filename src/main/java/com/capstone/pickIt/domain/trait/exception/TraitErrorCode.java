package com.capstone.pickIt.domain.trait.exception;

import com.capstone.pickIt.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TraitErrorCode implements BaseCode {

    TRAIT_NOT_FOUND(HttpStatus.NOT_FOUND, "TRAIT404", "존재하지 않는 성향 항목입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}