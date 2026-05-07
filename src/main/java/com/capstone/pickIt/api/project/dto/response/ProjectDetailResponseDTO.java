package com.capstone.pickIt.api.project.dto.response;

import com.capstone.pickIt.domain.project.entity.ProjectTeamStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProjectDetailResponseDTO(
        Long projectTeamId,
        Long courseId,
        String courseName,
        ProjectTeamStatus status,
        BigDecimal progressRate,
        LocalDateTime startedAt,
        LocalDateTime endedAt
) {
}