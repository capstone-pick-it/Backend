package com.capstone.pickIt.domain.teamlevel.exception;

import com.capstone.pickIt.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TeamLevelErrorCode implements BaseCode {

    TEAM_LEVEL_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAMLEVEL404_1", "팀플레벨 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}