package com.capstone.pickIt.api.chat.dto.response;

import com.capstone.pickIt.domain.chat.entity.ChatType;
import com.capstone.pickIt.domain.chat.entity.MessageType;
import com.capstone.pickIt.domain.project.entity.TeamRequestRole;
import com.capstone.pickIt.domain.project.entity.TeamRequestStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ChatMessageResponseDTO {

    public record ListResponse(
            Long chatRoomId,
            ChatType chatType,
            String roomName,
            Integer participantCount,
            ChatRoomResponseDTO.Opponent opponent,
            TeamRequestInfo teamRequest,
            List<MessageSummary> messages,
            Long nextCursor,
            boolean hasNext
    ) {
    }

    public record TeamRequestInfo(
            Long teamRequestId,
            TeamRequestStatus status,
            TeamRequestRole role
    ) {
    }

    public record MessageSummary(
            Long messageId,
            Sender sender,
            MessageType messageType,
            String content,
            List<FileInfo> files,
            LocalDateTime createdAt,
            boolean isMine,
            long unreadMemberCount
    ) {
    }

    public record Sender(
            Long userId,
            String nickname
    ) {
    }

    public record FileInfo(
            Long fileId,
            String fileName,
            String fileUrl,
            Long fileSize,
            String contentType
    ) {
    }
}
