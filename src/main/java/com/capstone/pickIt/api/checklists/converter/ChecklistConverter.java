package com.capstone.pickIt.api.checklists.converter;

import com.capstone.pickIt.api.checklists.dto.response.ChecklistItemResponseDTO;
import com.capstone.pickIt.domain.project.entity.ChecklistItem;
import com.capstone.pickIt.domain.user.entity.User;

public class ChecklistConverter {

    private ChecklistConverter() {
    }

    public static ChecklistItemResponseDTO toResponse(ChecklistItem checklistItem) {
        return new ChecklistItemResponseDTO(
                checklistItem.getId(),
                checklistItem.getProjectTeam().getId(),
                checklistItem.getTitle(),
                checklistItem.getDueDate(),
                checklistItem.getStatus(),
                toUserSummary(checklistItem.getManager()),
                toUserSummary(checklistItem.getCreatedByUser()),
                toUserSummary(checklistItem.getCompletedByUser()),
                checklistItem.getCompletedAt()
        );
    }

    private static ChecklistItemResponseDTO.UserSummaryDTO toUserSummary(User user) {
        if (user == null) {
            return null;
        }

        return new ChecklistItemResponseDTO.UserSummaryDTO(
                user.getId(),
                user.getNickname(),
                user.getMajor()
        );
    }
}