package com.capstone.pickIt.api.project.controller;

import com.capstone.pickIt.api.project.dto.response.ProjectDetailResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectListItemResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectMemberListResponseDTO;
import com.capstone.pickIt.api.project.service.ProjectService;
import com.capstone.pickIt.domain.project.entity.ProjectTeamStatus;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Project", description = "프로젝트 워크스페이스 API")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(
            summary = "내 팀플 목록 조회",
            description = """
                    로그인한 사용자가 참여 중인 팀 프로젝트 목록을 조회합니다.

                    **status 파라미터로 탭별 필터링 가능:**
                    - `RECRUITING` : 모집중인 팀 (아직 팀원을 구하는 중)
                    - `IN_PROGRESS` : 진행중인 팀 (팀 구성 완료, 프로젝트 진행 중)
                    - `DONE` : 완료된 팀
                    - 파라미터 없이 요청 시 → 전체 목록 반환

                    **조회 조건:**
                    - 탈퇴(leftAt != null)한 팀은 제외됩니다.
                    - 가입 승인(CONFIRMED) 상태인 팀만 조회됩니다.
                    - 가입 신청 대기(PENDING) 또는 거절(REJECTED) 상태는 포함되지 않습니다.
                    - 최신 참여순(joinedAt DESC)으로 정렬됩니다.

                    **인증 필요:** `Authorization: Bearer {ACCESS_TOKEN}` 헤더 필수
                    """,
            security = @SecurityRequirement(name = "JWT TOKEN")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "성공입니다.",
                                      "result": [
                                        {
                                          "projectId": 1,
                                          "projectName": "캡스톤디자인",
                                          "courseName": "캡스톤디자인",
                                          "currentMembers": 4,
                                          "projectStatus": "IN_PROGRESS",
                                          "memberNames": ["김철수", "이영희", "박민준", "최수진"]
                                        },
                                        {
                                          "projectId": 2,
                                          "projectName": "클라우드컴퓨팅",
                                          "courseName": "클라우드컴퓨팅",
                                          "currentMembers": 3,
                                          "projectStatus": "RECRUITING",
                                          "memberNames": ["홍길동", "김영수", "이지은"]
                                        }
                                      ]
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 로그인이 필요하거나 토큰이 만료된 경우",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMMON401",
                                      "message": "인증이 필요합니다.",
                                      "result": null
                                    }
                                    """)
                    )
            )
    })
    @GetMapping
    public ApiResponse<List<ProjectListItemResponseDTO>> getUserProjectList(
            @Parameter(
                    description = "프로젝트 상태 필터 (생략 시 전체 조회)",
                    schema = @Schema(allowableValues = {"RECRUITING", "IN_PROGRESS", "DONE"}),
                    example = "IN_PROGRESS"
            )
            @RequestParam(required = false) ProjectTeamStatus status
    ) {
        return ApiResponse.onSuccess(
                SuccessCode.OK,
                projectService.getUserProjectList(status)
        );
    }

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
