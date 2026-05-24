package com.capstone.pickIt.api.project.controller;

import com.capstone.pickIt.api.project.dto.request.CompletionDecisionRequestDTO;
import com.capstone.pickIt.api.project.dto.response.CompletionRequestResponseDTO;
import com.capstone.pickIt.api.project.service.ProjectCompletionService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Project Completion", description = "프로젝트 종료 요청 API")
@RestController
@RequiredArgsConstructor
public class ProjectCompletionController {

    private final ProjectCompletionService projectCompletionService;

    @Operation(summary = "프로젝트 종료 요청 생성", description = "프로젝트 팀 종료 요청을 생성합니다.")
    @PostMapping("/projects/{projectTeamId}/completion-requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CompletionRequestResponseDTO> createCompletionRequest(
            @PathVariable Long projectTeamId
    ) {
        CompletionRequestResponseDTO response =
                projectCompletionService.createCompletionRequest(projectTeamId);

        return ApiResponse.onSuccess(SuccessCode.CREATED, response);
    }

    @Operation(summary = "프로젝트 종료 요청 조회", description = "현재 진행 중인 프로젝트 종료 요청을 조회합니다.")
    @GetMapping("/projects/{projectTeamId}/completion-requests/current")
    public ApiResponse<CompletionRequestResponseDTO> getCurrentCompletionRequest(
            @PathVariable Long projectTeamId
    ) {
        CompletionRequestResponseDTO response =
                projectCompletionService.getCurrentCompletionRequest(projectTeamId);

        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @Operation(summary = "프로젝트 종료 요청 동의/거절", description = "프로젝트 종료 요청에 대해 동의 또는 거절합니다.")
    @PostMapping("/completion-requests/{completionRequestId}/decisions")
    public ApiResponse<CompletionRequestResponseDTO> decideCompletionRequest(
            @PathVariable Long completionRequestId,
            @Valid @RequestBody CompletionDecisionRequestDTO request
    ) {
        CompletionRequestResponseDTO response =
                projectCompletionService.decideCompletionRequest(completionRequestId, request);

        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }
}