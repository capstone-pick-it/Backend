package com.capstone.pickIt.api.checklists.dto.response;

import com.capstone.pickIt.domain.project.entity.ChecklistStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ChecklistItemResponseDTO(
        Long checklistItemId,
        Long projectTeamId,
        String title,
        LocalDate dueDate,
        ChecklistStatus status,
        UserSummaryDTO manager,
        UserSummaryDTO createdBy,
        UserSummaryDTO completedBy,
        LocalDateTime completedAt
) {
    public record UserSummaryDTO(
            Long userId,
            String nickname,
            String major
    ) {
    }
}