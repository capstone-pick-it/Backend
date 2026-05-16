package com.capstone.pickIt.api.checklists.dto.request;

import com.capstone.pickIt.domain.project.entity.ChecklistStatus;
import jakarta.validation.constraints.NotNull;

public record ChecklistStatusUpdateRequestDTO(
        @NotNull
        ChecklistStatus status
) {
}