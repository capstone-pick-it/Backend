package com.capstone.pickIt.domain.user.exception;

import com.capstone.pickIt.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseCode {

    USER_EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "USER400_1", "이메일 인증이 완료되지 않은 사용자입니다."),
    EMAIL_CODE_INVALID(HttpStatus.BAD_REQUEST, "USER400_2", "인증 코드가 올바르지 않거나 만료되었습니다."),
    EMAIL_DOMAIN_INVALID(HttpStatus.BAD_REQUEST, "USER400_3", "학교 이메일(@ac.kr)만 사용 가능합니다."),
    PASSWORD_RESET_CODE_INVALID(HttpStatus.BAD_REQUEST, "USER400_4", "비밀번호 재설정 인증 코드가 올바르지 않거나 만료되었습니다."),
    PASSWORD_RESET_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "USER400_5", "비밀번호 재설정 인증이 완료되지 않았습니다."),

    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "USER401_1", "비밀번호가 올바르지 않습니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "USER401_2", "이메일 또는 비밀번호가 올바르지 않습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "USER401_3", "유효하지 않거나 만료된 토큰입니다."),

    UNAUTHORIZED_USER(HttpStatus.FORBIDDEN, "USER403_1", "해당 사용자에 대한 접근 권한이 없습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_1", "사용자를 찾을 수 없습니다."),

    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER409_1", "이미 존재하는 사용자입니다."),
    ALREADY_WITHDRAWN(HttpStatus.CONFLICT, "USER409_2", "이미 탈퇴한 사용자입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
