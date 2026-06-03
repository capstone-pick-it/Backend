package com.capstone.pickIt.api.project.dto.response;

import com.capstone.pickIt.domain.project.entity.ProjectTeamMemberRole;
import com.capstone.pickIt.domain.project.entity.RecruitmentConfirmStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectMemberSummaryDTO(
        Long projectTeamMemberId,
        Long userId,
        String nickname,
        String major,
        Integer grade,
        ProjectTeamMemberRole role,
        RecruitmentConfirmStatus recruitmentConfirmStatus,
        LocalDateTime joinedAt,
        Integer point,
        Integer teamLevel,
        String importanceLevel,
        List<String> traits
) {
    public ProjectMemberSummaryDTO {
        traits = (traits == null) ? List.of() : List.copyOf(traits);
    }
}
