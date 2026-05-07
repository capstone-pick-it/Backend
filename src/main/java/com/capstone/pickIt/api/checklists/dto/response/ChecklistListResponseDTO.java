package com.capstone.pickIt.api.checklists.dto.response;

import java.util.List;

public record ChecklistListResponseDTO(
        Long projectTeamId,
        List<ChecklistItemResponseDTO> checklists
) {
}