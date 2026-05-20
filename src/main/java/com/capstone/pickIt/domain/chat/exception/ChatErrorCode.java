package com.capstone.pickIt.domain.chat.exception;

import com.capstone.pickIt.global.apiPayload.response.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements BaseCode {

    CANNOT_CHAT_WITH_SELF(
            HttpStatus.BAD_REQUEST,
            "CHAT400_1",
            "자기 자신과는 채팅방을 생성할 수 없습니다."
    ),
    ONLY_DIRECT_CHAT_CAN_REQUEST_TEAM(
            HttpStatus.BAD_REQUEST,
            "CHAT400_2",
            "1:1 채팅방에서만 팀원 요청을 보낼 수 있습니다."
    ),
    NOT_COMMON_COURSE(
            HttpStatus.BAD_REQUEST,
            "CHAT400_3",
            "두 사용자의 공통 과목이 아닙니다."
    ),
    NOT_CHAT_ROOM_PARTICIPANT(
            HttpStatus.FORBIDDEN,
            "CHAT403_1",
            "해당 채팅방의 참여자가 아닙니다."
    ),
    CURRENT_USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHAT404_1",
            "현재 사용자를 찾을 수 없습니다."
    ),
    TARGET_USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHAT404_2",
            "상대 사용자를 찾을 수 없습니다."
    ),
    CHAT_PART_NOT_FOUND(
            HttpStatus.NOT_FOUND,
        "CHAT404_3",
            "채팅 참여 정보를 찾을 수 없습니다."
    ),
    CHAT_ROOM_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHAT404_4",
            "채팅방을 찾을 수 없습니다."
    ),
    COURSE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHAT404_5",
            "과목을 찾을 수 없습니다."
    ),
    PENDING_REQUEST_EXISTS_FOR_COURSE(
            HttpStatus.CONFLICT,
            "CHAT409_1",
            "같은 과목에 대해 대기 중인 팀원 요청이 이미 존재합니다."
    ),
    PENDING_REQUEST_EXISTS_FOR_RECEIVER(
            HttpStatus.CONFLICT,
            "CHAT409_2",
            "해당 사용자에게 이미 대기 중인 팀원 요청을 보냈습니다."
    ),
    PENDING_TEAM_REQUEST_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
        "CHAT409_3",
            "대기 중인 팀원 요청이 이미 존재합니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
