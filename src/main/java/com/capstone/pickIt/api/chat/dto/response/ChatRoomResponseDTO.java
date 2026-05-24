package com.capstone.pickIt.api.chat.dto.response;

import com.capstone.pickIt.domain.chat.entity.ChatBadgeType;
import com.capstone.pickIt.domain.chat.entity.ChatType;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomResponseDTO {

    public record ListResponse(
            List<ChatRoomSummary> chatRooms,
            Cursor nextCursor,
            boolean hasNext
    ) {
    }

    public record ChatRoomSummary(
            Long chatRoomId,
            ChatType chatType,
            Opponent opponent,
            String roomName,
            int participantCount,
            String lastMessage,
            LocalDateTime lastMessageAt,
            long unreadCount,
            ChatBadgeType badgeType
    ) {
    }

    public record Opponent(
            Long userId,
            String nickname
    ) {
    }

    public record Cursor(
            LocalDateTime lastMessageAt,
            Long chatRoomId
    ) {
    }

    public record LeaveResponse(
            Long chatRoomId,
            ChatType chatType,
            LocalDateTime deletedAt
    ) {
    }
}
