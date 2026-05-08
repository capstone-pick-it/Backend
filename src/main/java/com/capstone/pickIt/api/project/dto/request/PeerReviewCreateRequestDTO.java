package com.capstone.pickIt.api.project.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PeerReviewCreateRequestDTO(
        @NotNull
        @Positive
        Long revieweeUserId,

        @NotNull
        @Min(1)
        @Max(5)
        Integer completionScore,

        @NotNull
        @Min(1)
        @Max(5)
        Integer proactivityScore,

        @NotNull
        @Min(1)
        @Max(5)
        Integer satisfactionScore
) {
}