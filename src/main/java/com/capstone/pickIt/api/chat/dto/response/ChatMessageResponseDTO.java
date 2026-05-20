package com.capstone.pickIt.api.chat.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class ChatMessageResponseDTO {

    public record MessageBroadcast(
            Long messageId,
            Long chatRoomId,
            Long senderId,
            String senderNickname,
            String messageType,
            String content,
            List<FileInfo> files,
            LocalDateTime createdAt
    ) {
    }

    public record FileInfo(
            Long messageFileId,
            String fileUrl,
            String fileName,
            Long fileSize,
            String contentType
    ) {
    }
}
