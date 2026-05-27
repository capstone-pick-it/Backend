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
    TEAM_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT404_2", "팀 멤버를 찾을 수 없습니다."),
    LEAVE_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT404_3", "나가기 요청을 찾을 수 없습니다."),

    NOT_PENDING_MEMBER(HttpStatus.BAD_REQUEST, "PROJECT400_1", "PENDING 상태의 멤버만 나가기 가능합니다."),
    NOT_CONFIRMED_MEMBER(HttpStatus.BAD_REQUEST, "PROJECT400_2", "CONFIRMED 상태의 멤버만 이 작업을 수행할 수 있습니다."),
    ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "PROJECT400_3", "이미 확정한 멤버입니다."),
    CONFIRM_WINDOW_EXPIRED(HttpStatus.BAD_REQUEST, "PROJECT400_4", "확정 가능 기간(48시간)이 초과되었습니다."),
    LEAVE_REQUEST_ALREADY_EXISTS(HttpStatus.CONFLICT, "PROJECT409_1", "이미 진행 중인 나가기 요청이 있습니다."),
    ALREADY_APPROVED_LEAVE(HttpStatus.CONFLICT, "PROJECT409_2", "이미 인정한 나가기 요청입니다."),
    CANNOT_APPROVE_OWN_LEAVE_REQUEST(HttpStatus.FORBIDDEN, "PROJECT403_2", "본인의 나가기 요청은 인정할 수 없습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}