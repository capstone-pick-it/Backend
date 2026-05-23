package com.capstone.pickIt.api.project.dto.response;

import com.capstone.pickIt.api.checklists.dto.response.ChecklistItemResponseDTO;
import com.capstone.pickIt.domain.project.entity.ProjectTeamStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectDetailResponseDTO(
        Long projectTeamId,
        Long courseId,
        String courseName,
        String semester,
        ProjectTeamStatus status,
        BigDecimal progressRate,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        List<ProjectMemberSummaryDTO> members,
        List<ChecklistItemResponseDTO> checklists
) {
}