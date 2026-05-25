package com.capstone.pickIt.api.project.controller;

import com.capstone.pickIt.api.project.dto.response.ConfirmResponseDTO;
import com.capstone.pickIt.api.project.dto.response.TeamLeaveRequestResponseDTO;
import com.capstone.pickIt.api.project.service.TeamMemberService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Team Member", description = "팀원 확정 및 나가기 API")
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @Operation(summary = "팀원 확정", description = "PENDING 상태에서 확정 버튼을 누릅니다. 전원 확정 시 팀이 IN_PROGRESS로 전환됩니다.")
    @PatchMapping("/{projectTeamId}/confirm")
    public ApiResponse<ConfirmResponseDTO> confirm(
            @PathVariable Long projectTeamId) {
        return ApiResponse.onSuccess(SuccessCode.OK, teamMemberService.confirm(projectTeamId));
    }

    @Operation(summary = "팀 나가기 (PENDING)", description = "확정 전(PENDING) 상태에서 팀을 나갑니다. 패널티 없음.")
    @DeleteMapping("/{projectTeamId}/leave")
    public ApiResponse<Void> leavePending(
            @PathVariable Long projectTeamId) {
        teamMemberService.leavePending(projectTeamId);
        return ApiResponse.onSuccess(SuccessCode.OK, null);
    }

    @Operation(summary = "나가기 요청 (CONFIRMED)", description = "확정 후 팀원 전원의 동의를 받아 나가기를 요청합니다.")
    @PostMapping("/{projectTeamId}/leave/request")
    public ApiResponse<TeamLeaveRequestResponseDTO> requestLeave(
            @PathVariable Long projectTeamId) {
        return ApiResponse.onSuccess(SuccessCode.CREATED, teamMemberService.requestLeave(projectTeamId));
    }

    @Operation(summary = "나가기 요청 조회", description = "팀의 PENDING 나가기 요청을 조회합니다. 요청이 없으면 null을 반환합니다.")
    @GetMapping("/{projectTeamId}/leave/request")
    public ApiResponse<TeamLeaveRequestResponseDTO> getLeaveRequest(
            @PathVariable Long projectTeamId) {
        return ApiResponse.onSuccess(SuccessCode.OK, teamMemberService.getLeaveRequest(projectTeamId));
    }

    @Operation(summary = "나가기 인정", description = "다른 팀원의 나가기 요청에 동의합니다. 전원 동의 시 해당 팀원이 팀을 나갑니다.")
    @PostMapping("/{projectTeamId}/leave/approve")
    public ApiResponse<Void> approveLeave(
            @PathVariable Long projectTeamId) {
        teamMemberService.approveLeave(projectTeamId);
        return ApiResponse.onSuccess(SuccessCode.OK, null);
    }

    @Operation(summary = "강제 나가기 (CONFIRMED)", description = "확정 후 동의 없이 팀을 나갑니다. 포인트 감점 패널티가 적용됩니다.")
    @DeleteMapping("/{projectTeamId}/leave/force")
    public ApiResponse<Void> leaveForce(
            @PathVariable Long projectTeamId) {
        teamMemberService.leaveForce(projectTeamId);
        return ApiResponse.onSuccess(SuccessCode.OK, null);
    }
}
