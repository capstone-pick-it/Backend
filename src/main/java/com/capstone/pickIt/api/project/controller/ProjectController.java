package com.capstone.pickIt.api.project.controller;

import com.capstone.pickIt.api.project.dto.response.ProjectDetailResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectMemberListResponseDTO;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @GetMapping("/{projectTeamId}")
    public ApiResponse<ProjectDetailResponseDTO> getProjectDetail(
            @PathVariable Long projectTeamId
    ) {
        // TODO: Service 연동
        ProjectDetailResponseDTO response = null;

        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @GetMapping("/{projectTeamId}/members")
    public ApiResponse<ProjectMemberListResponseDTO> getProjectMembers(
            @PathVariable Long projectTeamId
    ) {
        // TODO: Service 연동
        ProjectMemberListResponseDTO response = null;

        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @PostMapping("/{projectTeamId}/leave")
    public ApiResponse<Void> leaveProject(
            @PathVariable Long projectTeamId
    ) {
        // TODO: Service 연동

        return ApiResponse.onSuccess(SuccessCode.OK, null);
    }
}