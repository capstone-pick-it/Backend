package com.capstone.pickIt.api.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public class ChatMessageRequestDTO {

    public record ReadUpdateRequest(
            @NotNull Long lastReadMessageId
    ) {
    }
}
