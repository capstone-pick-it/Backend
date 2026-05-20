package com.capstone.pickIt.api.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public record TeamRequestCreateRequestDTO(

        @NotNull(message = "courseId는 필수입니다.")
        Long courseId
) {
}
