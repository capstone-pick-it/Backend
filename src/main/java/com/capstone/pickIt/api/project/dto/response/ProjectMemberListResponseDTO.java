package com.capstone.pickIt.api.project.dto.response;

import java.util.List;

public record ProjectMemberListResponseDTO(
        Long projectTeamId,
        List<ProjectMemberSummaryDTO> members
) {
}