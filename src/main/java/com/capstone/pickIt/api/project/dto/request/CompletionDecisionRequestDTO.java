package com.capstone.pickIt.api.project.dto.request;

import com.capstone.pickIt.domain.project.entity.CompletionDecision;
import jakarta.validation.constraints.NotNull;

public record CompletionDecisionRequestDTO(
        @NotNull
        CompletionDecision decision
) {
}