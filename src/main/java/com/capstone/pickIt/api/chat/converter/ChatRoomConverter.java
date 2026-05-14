package com.capstone.pickIt.api.chat.converter;

import com.capstone.pickIt.api.chat.dto.response.DirectChatRoomResponseDTO;
import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import com.capstone.pickIt.domain.user.entity.User;

public class ChatRoomConverter {

    private ChatRoomConverter() {
    }

    public static DirectChatRoomResponseDTO.CreateOrEnter toCreateOrEnterResponse(
            ChatRoom chatRoom,
            User opponent,
            boolean isNew
    ) {
        return new DirectChatRoomResponseDTO.CreateOrEnter(
                chatRoom.getId(),
                chatRoom.getChatType().name(),
                isNew,
                new DirectChatRoomResponseDTO.Opponent(
                        opponent.getId(),
                        opponent.getNickname()
                )
        );
    }
}
