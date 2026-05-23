package com.capstone.pickIt.domain.project.exception;

import com.capstone.pickIt.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProjectErrorCode implements BaseCode {

    NOT_PROJECT_MEMBER(HttpStatus.FORBIDDEN, "PROJECT403_1", "해당 프로젝트 팀의 멤버만 접근할 수 있습니다."),

    PROJECT_TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT404_1", "프로젝트 팀을 찾을 수 없습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}