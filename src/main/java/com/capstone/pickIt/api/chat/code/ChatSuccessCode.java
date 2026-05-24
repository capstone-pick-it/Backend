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
    ),
    TEAM_REQUEST_CREATED(
            HttpStatus.OK,
            "CHAT200_2",
            "팀원 요청 전송에 성공했습니다."
    ),
    COMMON_COURSE_LIST_FETCHED(
            HttpStatus.OK,
            "CHAT200_3",
            "공통 과목 목록 조회에 성공했습니다."
    ),
    TEAM_REQUEST_RESPONDED(
            HttpStatus.OK,
            "CHAT200_4",
            "팀원 요청 응답 처리에 성공했습니다."
    ),
    CHAT_ROOM_LIST_FOUND(
            HttpStatus.OK,
            "CHAT200_5",
            "채팅방 목록 조회에 성공했습니다."
    ),
    CHAT_MESSAGE_LIST_FETCHED(
            HttpStatus.OK,
            "CHAT200_6",
            "채팅방 메시지 목록 조회에 성공했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
