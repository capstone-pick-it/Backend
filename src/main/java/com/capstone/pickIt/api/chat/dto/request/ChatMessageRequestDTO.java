package com.capstone.pickIt.api.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ChatMessageRequestDTO {

    public record ReadUpdateRequest(
            @NotNull @Positive Long lastReadMessageId
    ) {
    }
}
