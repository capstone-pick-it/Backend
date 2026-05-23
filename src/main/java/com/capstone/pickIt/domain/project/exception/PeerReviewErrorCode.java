package com.capstone.pickIt.domain.project.exception;

import com.capstone.pickIt.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PeerReviewErrorCode implements BaseCode {

    CANNOT_REVIEW_SELF(HttpStatus.BAD_REQUEST,"PEER_REVIEW400_1","자기 자신은 평가할 수 없습니다."),
    PROJECT_NOT_COMPLETED(HttpStatus.BAD_REQUEST,"PEER_REVIEW400_2","팀플 종료 후 평가 가능합니다."),

    NOT_PROJECT_MEMBER(HttpStatus.FORBIDDEN,"PEER_REVIEW403_1","프로젝트 팀 멤버만 접근할 수 있습니다."),

    PROJECT_TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "PEER_REVIEW404_1", "프로젝트 팀을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"PEER_REVIEW404_2", "사용자를 찾을 수 없습니다."),

    ALREADY_REVIEWED(HttpStatus.CONFLICT, "PEER_REVIEW409_1","이미 평가한 팀원입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}