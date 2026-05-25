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
    TEAM_REQUEST_NOT_PENDING(
            HttpStatus.BAD_REQUEST,
            "CHAT400_4",
            "대기 중인 팀원 요청만 처리할 수 있습니다."
    ),
    TEAM_REQUEST_EXPIRED(
            HttpStatus.BAD_REQUEST,
            "CHAT400_5",
            "24시간이 지나 만료된 팀원 요청입니다."
    ),
    INVALID_MESSAGE_CONTENT(
            HttpStatus.BAD_REQUEST,
            "CHAT400_6",
            "텍스트 메시지는 content가 필요합니다."
    ),
    MESSAGE_FILE_NOT_ALLOWED(
            HttpStatus.BAD_REQUEST,
            "CHAT400_7",
            "텍스트 메시지는 파일을 함께 보낼 수 없습니다."
    ),
    MESSAGE_CONTENT_NOT_ALLOWED(
            HttpStatus.BAD_REQUEST,
            "CHAT400_8",
            "파일 메시지는 content를 함께 보낼 수 없습니다."
    ),
    MESSAGE_FILE_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "CHAT400_9",
            "파일 메시지는 파일 정보가 필요합니다."
    ),
    INVALID_MESSAGE_TYPE(
            HttpStatus.BAD_REQUEST,
            "CHAT400_10",
            "지원하지 않는 메시지 타입입니다."
    ),
    INSUFFICIENT_POINT(
            HttpStatus.BAD_REQUEST,
            "CHAT400_11",
            "팀원 요청에 필요한 포인트가 부족합니다."
    ),
    INVALID_CURSOR(
            HttpStatus.BAD_REQUEST,
            "CHAT400_12",
            "커서 값이 올바르지 않습니다."
    ),
    CANNOT_LEAVE_IN_PROGRESS_GROUP_CHAT(
            HttpStatus.BAD_REQUEST,
            "CHAT400_13",
            "진행 중인 팀 채팅방은 나갈 수 없습니다."
    ),
    ALREADY_LEFT_CHAT_ROOM(
            HttpStatus.BAD_REQUEST,
            "CHAT400_14",
            "이미 나간 채팅방입니다."
    ),
    FILE_COUNT_EXCEEDED(
            HttpStatus.BAD_REQUEST,
            "CHAT400_15",
            "파일은 최대 5개까지 업로드할 수 있습니다."
    ),
    INVALID_FILE_TYPE(
            HttpStatus.BAD_REQUEST,
            "CHAT400_16",
            "지원하지 않는 파일 형식입니다."
    ),
    FILE_SIZE_EXCEEDED(
            HttpStatus.BAD_REQUEST,
            "CHAT400_17",
            "파일 크기는 20MB를 초과할 수 없습니다."
    ),
    INVALID_FILE_URL(
            HttpStatus.BAD_REQUEST,
            "CHAT400_18",
            "유효하지 않은 파일 경로입니다."
    ),
    NOT_CHAT_ROOM_PARTICIPANT(
            HttpStatus.FORBIDDEN,
            "CHAT403_1",
            "해당 채팅방의 참여자가 아닙니다."
    ),
    NOT_TEAM_REQUEST_RECEIVER(
            HttpStatus.FORBIDDEN,
            "CHAT403_2",
            "해당 팀원 요청을 처리할 권한이 없습니다."
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
    TEAM_REQUEST_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHAT404_6",
            "팀원 요청을 찾을 수 없습니다."
    ),
    USER_COURSE_PROFILE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHAT404_7",
            "사용자 과목 프로필을 찾을 수 없습니다."
    ),
    MESSAGE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHAT404_8",
            "메시지를 찾을 수 없습니다."
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
    ),
    INVALID_RECRUITMENT_STATUS(
            HttpStatus.CONFLICT,
            "CHAT409_4",
            "현재 모집 가능한 상태가 아닙니다."
    ),
    ALREADY_JOINED_ACTIVE_TEAM(
            HttpStatus.CONFLICT,
            "CHAT409_5",
            "이미 해당 과목의 모집 중이거나 진행 중인 팀에 참여 중입니다."
    ),
    FILE_UPLOAD_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "CHAT500_1",
            "파일 업로드에 실패했습니다."
    ),
    FILE_URL_GENERATION_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "CHAT500_2",
            "파일 URL 생성에 실패했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
