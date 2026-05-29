package com.capstone.pickIt.api.chat.dto.response;

import com.capstone.pickIt.domain.chat.entity.ChatType;
import com.capstone.pickIt.domain.chat.entity.MessageType;

import java.time.LocalDateTime;

public class ChatNotificationResponseDTO {

    public record ChatRoomNotification(
            String eventType,
            Long chatRoomId,
            ChatType chatType,
            Long messageId,
            MessageType messageType,
            String lastMessage,
            LocalDateTime lastMessageAt,
            Long senderId,
            String senderNickname,
            Long unreadCount
    ) {
    }
}
