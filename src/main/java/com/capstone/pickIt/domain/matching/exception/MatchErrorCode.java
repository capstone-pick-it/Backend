package com.capstone.pickIt.domain.matching.exception;

import com.capstone.pickIt.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MatchErrorCode implements BaseCode {

    TARGET_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCH404", "존재하지 않는 모집 프로필입니다."),
    CANNOT_MATCH_OWN_PROFILE(HttpStatus.BAD_REQUEST, "MATCH400_1", "자신의 프로필과는 매칭 점수를 계산할 수 없습니다."),
    PROFILE_NOT_RECRUITING(HttpStatus.BAD_REQUEST, "MATCH400_2", "모집 중인 프로필에만 매칭 점수를 계산할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
