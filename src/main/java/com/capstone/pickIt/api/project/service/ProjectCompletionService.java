package com.capstone.pickIt.api.project.service;

import com.capstone.pickIt.api.project.dto.request.CompletionDecisionRequestDTO;
import com.capstone.pickIt.api.project.dto.response.CompletionRequestResponseDTO;

public interface ProjectCompletionService {

    CompletionRequestResponseDTO createCompletionRequest(Long projectTeamId);

    CompletionRequestResponseDTO getCurrentCompletionRequest(Long projectTeamId);

    CompletionRequestResponseDTO decideCompletionRequest(
            Long completionRequestId,
            CompletionDecisionRequestDTO request
    );
}