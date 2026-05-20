package com.capstone.pickIt.domain.project.exception;

import com.capstone.pickIt.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChecklistErrorCode implements BaseCode {

    MANAGER_NOT_PROJECT_MEMBER(HttpStatus.BAD_REQUEST, "CHECKLIST400_1", "담당자는 해당 프로젝트 팀의 멤버여야 합니다."),

    NOT_PROJECT_MEMBER(HttpStatus.FORBIDDEN, "CHECKLIST403_1", "해당 프로젝트 팀의 멤버만 체크리스트에 접근할 수 있습니다."),
    ONLY_MANAGER_CAN_UPDATE_CHECKLIST(HttpStatus.FORBIDDEN, "CHECKLIST403_2", "체크리스트 담당자만 수정할 수 있습니다."),

    PROJECT_TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHECKLIST404_1", "프로젝트 팀을 찾을 수 없습니다."),
    CHECKLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "CHECKLIST404_2", "체크리스트를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "CHECKLIST404_3", "사용자를 찾을 수 없습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}