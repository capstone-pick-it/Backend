package com.capstone.pickIt.api.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public record DirectChatRoomCreateRequestDTO(
        @NotNull(message = "상대 사용자 ID는 필수입니다.")
        Long targetUserId
) {
}
