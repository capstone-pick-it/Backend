package com.capstone.pickIt.api.project.converter;

import com.capstone.pickIt.api.checklists.dto.response.ChecklistItemResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectDetailResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectListItemResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectMemberListResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectMemberSummaryDTO;
import com.capstone.pickIt.domain.point.entity.Point;
import com.capstone.pickIt.domain.project.entity.ProjectTeam;
import com.capstone.pickIt.domain.project.entity.ProjectTeamMember;

import java.util.List;
import java.util.Map;

public class ProjectConverter {

    private ProjectConverter() {
    }

    public static ProjectDetailResponseDTO toDetailResponse(
            ProjectTeam projectTeam,
            List<ProjectMemberSummaryDTO> members,
            List<ChecklistItemResponseDTO> checklists
    ) {
        return new ProjectDetailResponseDTO(
                projectTeam.getId(),
                projectTeam.getCourse().getId(),
                projectTeam.getCourse().getCourseName(),
                projectTeam.getCourse().getSemester(),
                projectTeam.getStatus(),
                projectTeam.getProgressRate(),
                projectTeam.getStartedAt(),
                projectTeam.getEndedAt(),
                members,
                checklists
        );
    }

    public static ProjectMemberListResponseDTO toMemberListResponse(
            Long projectTeamId,
            List<ProjectMemberSummaryDTO> members
    ) {
        return new ProjectMemberListResponseDTO(projectTeamId, members);
    }

    public static ProjectListItemResponseDTO toListItem(ProjectTeam projectTeam, List<String> memberNames) {
        String projectName = projectTeam.getName() != null
                ? projectTeam.getName()
                : projectTeam.getCourse().getCourseName();
        int memberCount = memberNames.size();
        return new ProjectListItemResponseDTO(
                projectTeam.getId(),
                projectName,
                projectTeam.getCourse().getCourseName(),
                memberCount,
                memberCount,
                projectTeam.getStatus(),
                memberNames
        );
    }

    public static ProjectMemberSummaryDTO toMemberSummary(
            ProjectTeamMember member,
            Map<Long, Point> pointMap
    ) {
        Long userId = member.getUser().getId();
        Point point = pointMap.get(userId);

        return new ProjectMemberSummaryDTO(
                member.getId(),
                userId,
                member.getUser().getNickname(),
                member.getUser().getMajor(),
                member.getRole(),
                member.getRecruitmentConfirmStatus(),
                member.getJoinedAt(),
                point != null ? point.getBalance() : null,
                null,
                null,
                List.of()
        );
    }
}