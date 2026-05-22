package com.capstone.pickIt.api.chat.event;

import com.capstone.pickIt.api.chat.dto.response.ChatRoomEventResponseDTO;

public record ChatRoomBroadcastEvent(
        Long chatRoomId,
        ChatRoomEventResponseDTO.ChatRoomEvent payload
) {
}
