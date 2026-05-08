package com.capstone.pickIt.api.project.dto.response;

import com.capstone.pickIt.domain.project.entity.ProjectTeamMemberRole;
import com.capstone.pickIt.domain.project.entity.RecruitmentConfirmStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectMemberListResponseDTO(
        Long projectTeamId,
        List<ProjectMemberResponseDTO> members
) {
    public record ProjectMemberResponseDTO(
            Long projectTeamMemberId,
            Long userId,
            String nickname,
            String major,
            ProjectTeamMemberRole role,
            RecruitmentConfirmStatus recruitmentConfirmStatus,
            LocalDateTime joinedAt,
            LocalDateTime leftAt,
            boolean activeMember
    ) {
    }
}