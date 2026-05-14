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
            "CHAT4001",
            "자기 자신과는 채팅방을 생성할 수 없습니다."
    ),
    CURRENT_USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHAT4041",
            "현재 사용자를 찾을 수 없습니다."
    ),
    TARGET_USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHAT4042",
            "상대 사용자를 찾을 수 없습니다."
    ),
    CHAT_PART_NOT_FOUND(
            HttpStatus.NOT_FOUND,
        "CHAT4043",
            "채팅 참여 정보를 찾을 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
