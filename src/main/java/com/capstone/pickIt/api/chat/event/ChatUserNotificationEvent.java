package com.capstone.pickIt.api.chat.event;

import com.capstone.pickIt.api.chat.dto.response.ChatNotificationResponseDTO;

public record ChatUserNotificationEvent(
        Long receiverId,
        ChatNotificationResponseDTO.ChatRoomNotification payload
) {
}
