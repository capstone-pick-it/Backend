package com.capstone.pickIt.api.project.service;

import com.capstone.pickIt.api.project.dto.response.ProjectDetailResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectListItemResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectMemberListResponseDTO;
import com.capstone.pickIt.domain.project.entity.ProjectTeamStatus;

import java.util.List;

public interface ProjectService {

    ProjectDetailResponseDTO getProjectDetail(Long projectTeamId);

    ProjectMemberListResponseDTO getProjectMembers(Long projectTeamId);

    List<ProjectListItemResponseDTO> getUserProjectList(ProjectTeamStatus status);
}