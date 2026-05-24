package com.capstone.pickIt.api.chat.converter;

import com.capstone.pickIt.api.chat.dto.response.ChatMessageResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.DirectChatRoomResponseDTO;
import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import com.capstone.pickIt.domain.chat.entity.Message;
import com.capstone.pickIt.domain.user.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatRoomConverter {

    private ChatRoomConverter() {
    }

    public static DirectChatRoomResponseDTO.CreateOrEnter toCreateOrEnterResponse(
            ChatRoom chatRoom,
            User opponent,
            boolean isNew
    ) {
        Objects.requireNonNull(chatRoom, "chatRoom must not be null");
        Objects.requireNonNull(opponent, "opponent must not be null");

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
