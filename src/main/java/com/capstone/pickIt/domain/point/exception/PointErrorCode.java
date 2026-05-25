package com.capstone.pickIt.domain.point.exception;

import com.capstone.pickIt.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PointErrorCode implements BaseCode {

    INVALID_POINT_AMOUNT(HttpStatus.BAD_REQUEST, "POINT400_1", "포인트 금액은 1 이상이어야 합니다."),

    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "POINT404_1", "포인트 정보를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "POINT404_2", "사용자를 찾을 수 없습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
