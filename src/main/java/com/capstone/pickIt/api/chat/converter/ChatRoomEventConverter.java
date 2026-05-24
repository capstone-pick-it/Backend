package com.capstone.pickIt.api.chat.converter;

import com.capstone.pickIt.api.chat.dto.response.ChatRoomEventResponseDTO;
import com.capstone.pickIt.domain.chat.entity.ChatEventType;
import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import com.capstone.pickIt.domain.chat.entity.Message;
import com.capstone.pickIt.domain.chat.entity.MessageFile;
import com.capstone.pickIt.domain.project.entity.TeamRequest;

import java.util.List;

public class ChatRoomEventConverter {

    private ChatRoomEventConverter() {
    }

    public static ChatRoomEventResponseDTO.ChatRoomEvent toMessageEvent(
            ChatRoom chatRoom,
            Message message,
            List<MessageFile> files,
            int unreadMemberCount
    ) {
        return new ChatRoomEventResponseDTO.ChatRoomEvent(
                ChatEventType.CHAT_MESSAGE_CREATED.name(),
                chatRoom.getId(),
                chatRoom.getChatType().name(),
                new ChatRoomEventResponseDTO.MessagePayload(
                        message.getId(),
                        new ChatRoomEventResponseDTO.Sender(
                                message.getUser().getId(),
                                message.getUser().getNickname()
                        ),
                        message.getMessageType().name(),
                        message.getContent(),
                        files.stream()
                                .map(file -> new ChatRoomEventResponseDTO.FilePayload(
                                        file.getId(),
                                        file.getFileName(),
                                        file.getFileUrl(),
                                        file.getFileSize(),
                                        file.getContentType()
                                ))
                                .toList(),
                        message.getCreatedAt(),
                        unreadMemberCount
                ),
                null
        );
    }

    public static ChatRoomEventResponseDTO.ChatRoomEvent toTeamRequestCreatedEvent(
            TeamRequest teamRequest
    ) {
        return new ChatRoomEventResponseDTO.ChatRoomEvent(
                ChatEventType.TEAM_REQUEST_CREATED.name(),
                teamRequest.getChatRoom().getId(),
                teamRequest.getChatRoom().getChatType().name(),
                null,
                new ChatRoomEventResponseDTO.TeamRequestPayload(
                        teamRequest.getId(),
                        teamRequest.getSender().getId(),
                        teamRequest.getReceiver().getId(),
                        teamRequest.getTeamRequestStatus().name(),
                        teamRequest.getCreatedAt(),
                        null
                )
        );
    }

    public static ChatRoomEventResponseDTO.ChatRoomEvent toTeamRequestAcceptedEvent(
            TeamRequest teamRequest
    ) {
        return new ChatRoomEventResponseDTO.ChatRoomEvent(
                ChatEventType.TEAM_REQUEST_ACCEPTED.name(),
                teamRequest.getChatRoom().getId(),
                teamRequest.getChatRoom().getChatType().name(),
                null,
                new ChatRoomEventResponseDTO.TeamRequestPayload(
                        teamRequest.getId(),
                        teamRequest.getSender().getId(),
                        teamRequest.getReceiver().getId(),
                        teamRequest.getTeamRequestStatus().name(),
                        teamRequest.getCreatedAt(),
                        teamRequest.getRespondedAt()
                )
        );
    }

    public static ChatRoomEventResponseDTO.ChatRoomEvent toTeamRequestRejectedEvent(
            TeamRequest teamRequest
    ) {
        return new ChatRoomEventResponseDTO.ChatRoomEvent(
                ChatEventType.TEAM_REQUEST_REJECTED.name(),
                teamRequest.getChatRoom().getId(),
                teamRequest.getChatRoom().getChatType().name(),
                null,
                new ChatRoomEventResponseDTO.TeamRequestPayload(
                        teamRequest.getId(),
                        teamRequest.getSender().getId(),
                        teamRequest.getReceiver().getId(),
                        teamRequest.getTeamRequestStatus().name(),
                        teamRequest.getCreatedAt(),
                        teamRequest.getRespondedAt()
                )
        );
    }
}
