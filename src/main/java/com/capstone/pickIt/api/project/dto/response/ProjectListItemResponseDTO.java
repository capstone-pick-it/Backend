package com.capstone.pickIt.api.project.dto.response;

import com.capstone.pickIt.domain.project.entity.ProjectTeamStatus;

import java.util.List;

public record ProjectListItemResponseDTO(
        Long projectId,
        String projectName,
        String courseName,
        int memberCount,
        int currentMembers,
        ProjectTeamStatus projectStatus,
        List<String> memberNames
) {}