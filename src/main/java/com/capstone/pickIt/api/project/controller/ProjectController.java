package com.capstone.pickIt.api.project.controller;

import com.capstone.pickIt.api.project.dto.response.ProjectDetailResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectMemberListResponseDTO;
import com.capstone.pickIt.api.project.service.ProjectService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Project", description = "프로젝트 워크스페이스 API")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(
            summary = "프로젝트 상세 조회",
            description = "워크스페이스 홈 화면에 필요한 프로젝트 정보, 팀원 목록, 체크리스트를 통합 조회합니다."
    )
    @GetMapping("/{projectTeamId}")
    public ApiResponse<ProjectDetailResponseDTO> getProjectDetail(
            @PathVariable Long projectTeamId
    ) {
        ProjectDetailResponseDTO response =
                projectService.getProjectDetail(projectTeamId);

        return ApiResponse.onSuccess(
                SuccessCode.OK,
                response
        );
    }

    @Operation(
            summary = "팀 내부 팀원 리스트 조회",
            description = "프로젝트 팀 내부 팀원 목록을 조회합니다."
    )
    @GetMapping("/{projectTeamId}/members")
    public ApiResponse<ProjectMemberListResponseDTO> getProjectMembers(
            @PathVariable Long projectTeamId
    ) {
        ProjectMemberListResponseDTO response =
                projectService.getProjectMembers(projectTeamId);

        return ApiResponse.onSuccess(
                SuccessCode.OK,
                response
        );
    }

    @Operation(
            summary = "팀플 진행 중 나가기",
            description = "현재 참여 중인 프로젝트 팀에서 탈퇴합니다."
    )
    @PostMapping("/{projectTeamId}/leave")
    public ApiResponse<Void> leaveProject(
            @PathVariable Long projectTeamId
    ) {
        // TODO: Service 연결
        return ApiResponse.onSuccess(
                SuccessCode.OK,
                null
        );
    }
}