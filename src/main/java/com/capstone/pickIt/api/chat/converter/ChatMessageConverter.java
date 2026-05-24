package com.capstone.pickIt.api.chat.converter;

import com.capstone.pickIt.api.chat.dto.response.ChatMessageResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.ChatRoomResponseDTO;
import com.capstone.pickIt.domain.chat.entity.ChatPart;
import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import com.capstone.pickIt.domain.chat.entity.Message;
import com.capstone.pickIt.domain.project.entity.TeamRequest;
import com.capstone.pickIt.domain.project.entity.TeamRequestRole;

import java.util.List;
import java.util.Map;

public class ChatMessageConverter {

    public static ChatMessageResponseDTO.ListResponse toListResponse(
            ChatRoom chatRoom,
            Integer participantCount,
            ChatRoomResponseDTO.Opponent opponent,
            ChatMessageResponseDTO.TeamRequestInfo teamRequest,
            List<ChatMessageResponseDTO.MessageSummary> messages,
            Long nextCursor,
            boolean hasNext
    ) {
        return new ChatMessageResponseDTO.ListResponse(
                chatRoom.getId(),
                chatRoom.getChatType(),
                chatRoom.getRoomName(),
                participantCount,
                opponent,
                teamRequest,
                messages,
                nextCursor,
                hasNext
        );
    }

    public static ChatMessageResponseDTO.MessageSummary toMessageSummary(
            Message message,
            Long currentUserId,
            Map<Long, Long> unreadCountMap
    ) {
        return new ChatMessageResponseDTO.MessageSummary(
                message.getId(),
                new ChatMessageResponseDTO.Sender(
                        message.getUser().getId(),
                        message.getUser().getNickname()
                ),
                message.getMessageType(),
                message.getContent(),
                List.of(),
                message.getCreatedAt(),
                message.getUser().getId().equals(currentUserId),
                unreadCountMap.getOrDefault(message.getId(), 0L)
        );
    }

    public static ChatRoomResponseDTO.Opponent toOpponent(
            ChatPart opponentChatPart
    ) {
        return new ChatRoomResponseDTO.Opponent(
                opponentChatPart.getUser().getId(),
                opponentChatPart.getUser().getNickname()
        );
    }

    public static ChatMessageResponseDTO.TeamRequestInfo toTeamRequestInfo(
            TeamRequest teamRequest,
            Long currentUserId
    ) {
        TeamRequestRole role = teamRequest.getSender().getId().equals(currentUserId)
                ? TeamRequestRole.SENDER
                : TeamRequestRole.RECEIVER;

        return new ChatMessageResponseDTO.TeamRequestInfo(
                teamRequest.getId(),
                teamRequest.getTeamRequestStatus(),
                role
        );
    }

}
