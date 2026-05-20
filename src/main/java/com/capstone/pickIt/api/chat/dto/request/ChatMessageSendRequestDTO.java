package com.capstone.pickIt.api.chat.dto.request;

import com.capstone.pickIt.domain.chat.entity.MessageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ChatMessageSendRequestDTO(

        @NotNull(message = "chatRoomId는 필수입니다.")
        Long chatRoomId,

        @NotNull(message = "messageType은 필수입니다.")
        MessageType messageType,

        String content,

        @Valid
        List<FileInfo> files
) {
    public record FileInfo(
            @NotNull(message = "fileUrl은 필수입니다.")
            String fileUrl,

            @NotNull(message = "fileName은 필수입니다.")
            String fileName,

            Long fileSize,

            String contentType
    ) {
    }
}
