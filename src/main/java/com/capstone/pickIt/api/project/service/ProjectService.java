package com.capstone.pickIt.api.project.service;

import com.capstone.pickIt.api.project.dto.response.ProjectDetailResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectMemberListResponseDTO;

public interface ProjectService {

    ProjectDetailResponseDTO getProjectDetail(Long projectTeamId);

    ProjectMemberListResponseDTO getProjectMembers(Long projectTeamId);
}