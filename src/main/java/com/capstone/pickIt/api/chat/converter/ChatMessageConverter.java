package com.capstone.pickIt.api.chat.converter;

import com.capstone.pickIt.api.chat.dto.response.ChatMessageResponseDTO;
import com.capstone.pickIt.domain.chat.entity.Message;
import com.capstone.pickIt.domain.chat.entity.MessageFile;

import java.util.List;

public class ChatMessageConverter {

    private ChatMessageConverter() {
    }

    public static ChatMessageResponseDTO.MessageBroadcast toBroadcastResponse(
            Message message,
            List<MessageFile> files
    ) {
        return new ChatMessageResponseDTO.MessageBroadcast(
                message.getId(),
                message.getChatRoom().getId(),
                message.getUser().getId(),
                message.getUser().getNickname(),
                message.getMessageType().name(),
                message.getContent(),
                files.stream()
                        .map(file -> new ChatMessageResponseDTO.FileInfo(
                                file.getId(),
                                file.getFileUrl(),
                                file.getFileName(),
                                file.getFileSize(),
                                file.getContentType()
                        ))
                        .toList(),
                message.getCreatedAt()
        );
    }
}