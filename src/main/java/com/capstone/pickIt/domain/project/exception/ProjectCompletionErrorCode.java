package com.capstone.pickIt.domain.project.exception;

import com.capstone.pickIt.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProjectCompletionErrorCode implements BaseCode {

    PROJECT_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST, "COMPLETION400_1", "진행 중인 프로젝트만 종료 요청할 수 있습니다."),
    REQUESTER_CANNOT_DECIDE(HttpStatus.BAD_REQUEST, "COMPLETION400_2", "종료 요청자는 본인의 요청에 동의/거절할 수 없습니다."),

    NOT_PROJECT_MEMBER(HttpStatus.FORBIDDEN, "COMPLETION403_1", "해당 프로젝트 팀의 멤버만 접근할 수 있습니다."),

    PROJECT_TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPLETION404_1", "프로젝트 팀을 찾을 수 없습니다."),
    COMPLETION_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPLETION404_2", "프로젝트 종료 요청을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPLETION404_3", "사용자를 찾을 수 없습니다."),

    ALREADY_PENDING_COMPLETION_REQUEST(HttpStatus.CONFLICT, "COMPLETION409_1", "이미 진행 중인 종료 요청이 있습니다."),
    COMPLETION_REQUEST_ALREADY_FINALIZED(HttpStatus.CONFLICT, "COMPLETION409_2", "이미 종료된 요청입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}