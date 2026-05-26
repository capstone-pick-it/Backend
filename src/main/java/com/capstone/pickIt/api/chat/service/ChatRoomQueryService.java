package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.dto.response.ChatMessageResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.ChatRoomResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.CommonCourseResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.TeamRequestResponseDTO;

import java.time.LocalDateTime;

public interface ChatRoomQueryService {

    CommonCourseResponseDTO.CommonCourseList getCommonCourses(
            Long currentUserId,
            Long chatRoomId
    );

    ChatRoomResponseDTO.ListResponse getMyChatRooms(
            Long currentUserId,
            LocalDateTime cursorLastMessageAt,
            Long cursorChatRoomId
    );

    ChatMessageResponseDTO.ListResponse getChatMessages(
            Long currentUserId,
            Long chatRoomId,
            Long cursor
    );

    TeamRequestResponseDTO.LatestStatus getLatestTeamRequestStatus(
            Long currentUserId,
            Long chatRoomId
    );
}
