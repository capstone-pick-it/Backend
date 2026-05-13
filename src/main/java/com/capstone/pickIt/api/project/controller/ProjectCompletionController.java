package com.capstone.pickIt.api.project.controller;

import com.capstone.pickIt.api.project.dto.request.CompletionDecisionRequestDTO;
import com.capstone.pickIt.api.project.dto.response.CompletionRequestResponseDTO;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProjectCompletionController {

    @PostMapping("/projects/{projectTeamId}/completion-requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CompletionRequestResponseDTO> createCompletionRequest(
            @PathVariable Long projectTeamId
    ) {
        CompletionRequestResponseDTO response = null; // TODO: Service 연동

        return ApiResponse.onSuccess(SuccessCode.CREATED, response);
    }

    @GetMapping("/projects/{projectTeamId}/completion-requests/current")
    public ApiResponse<CompletionRequestResponseDTO> getCurrentCompletionRequest(
            @PathVariable Long projectTeamId
    ) {
        CompletionRequestResponseDTO response = null; // TODO: Service 연동
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @PostMapping("/completion-requests/{completionRequestId}/decisions")
    public ApiResponse<CompletionRequestResponseDTO> decideCompletionRequest(
            @PathVariable Long completionRequestId,
            @Valid @RequestBody CompletionDecisionRequestDTO request
    ) {
        CompletionRequestResponseDTO response = null; // TODO: Service 연동
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }
}