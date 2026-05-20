package com.capstone.pickIt.api.chat.dto.response;

public class DirectChatRoomResponseDTO {

    public record CreateOrEnter(
            Long chatRoomId,
            String chatType,
            Boolean isNew,
            Opponent opponent
    ) {
    }

    public record Opponent(
            Long userId,
            String nickname
    ) {
    }
}
