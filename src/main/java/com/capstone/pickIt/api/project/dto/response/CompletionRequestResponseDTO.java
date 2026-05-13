package com.capstone.pickIt.api.project.dto.response;

import com.capstone.pickIt.domain.project.entity.CompletionDecision;
import com.capstone.pickIt.domain.project.entity.CompletionRequestStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CompletionRequestResponseDTO(
        Long completionRequestId,
        Long projectTeamId,
        UserSummaryDTO requester,
        CompletionRequestStatus status,
        LocalDateTime requestedAt,
        LocalDateTime finalizedAt,
        List<CompletionApprovalResponseDTO> approvals
) {
    public record CompletionApprovalResponseDTO(
            Long completionApprovalId,
            UserSummaryDTO user,
            CompletionDecision decision,
            LocalDateTime decidedAt
    ) {
    }

    public record UserSummaryDTO(
            Long userId,
            String nickname,
            String major
    ) {
    }
}