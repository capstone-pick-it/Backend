package com.capstone.pickIt.api.chat.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomEventResponseDTO {

    public record ChatRoomEvent(
            String eventType,
            Long chatRoomId,
            String chatType,
            MessagePayload message,
            TeamRequestPayload teamRequest,
            MessageReadPayload messageRead
    ) {
    }

    public record MessagePayload(
            Long messageId,
            Sender sender,
            String messageType,
            String content,
            List<FilePayload> files,
            LocalDateTime createdAt,
            int unreadMemberCount
    ) {
    }

    public record Sender(
            Long userId,
            String nickname
    ) {
    }

    public record FilePayload(
            Long fileId,
            String fileName,
            String fileUrl,
            Long fileSize,
            String contentType
    ) {
    }

    public record TeamRequestPayload(
            Long teamRequestId,
            Long senderId,
            Long receiverId,
            String status,
            LocalDateTime createdAt,
            LocalDateTime respondedAt
    ) {
    }

    public record MessageReadPayload(
            Long readerId,
            Long lastReadMessageId
    ) {
    }
}
