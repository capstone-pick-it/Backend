package com.capstone.pickIt.domain.course.exception;

import com.capstone.pickIt.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProfileErrorCode implements BaseCode {

    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILE404", "존재하지 않는 프로필입니다."),
    PROFILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "PROFILE409", "해당 강의에 이미 프로필이 존재합니다."),
    PROFILE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PROFILE403", "해당 프로필에 접근 권한이 없습니다."),
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILE404_1", "존재하지 않는 강의입니다."),
    COURSE_HAS_ACTIVE_TEAM(HttpStatus.CONFLICT, "PROFILE409_1", "팀이 구성된 강의는 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
