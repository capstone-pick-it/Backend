package com.capstone.pickIt.api.project.dto.response;

import java.util.List;

public record ProjectMemberListResponseDTO(
        Long projectTeamId,
        List<ProjectMemberSummaryDTO> members
) {
    public ProjectMemberListResponseDTO {
        members = (members == null) ? List.of() : List.copyOf(members);
    }
}