package com.capstone.pickIt.api.chat.code;

import com.capstone.pickIt.global.apiPayload.response.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatSuccessCode implements BaseCode {

    DIRECT_CHAT_ROOM_CREATED_OR_ENTERED(
            HttpStatus.OK,
            "CHAT200_1",
            "1:1 채팅방 생성 또는 재입장에 성공했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
