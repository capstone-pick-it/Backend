package com.capstone.pickIt.api.project.converter;

import com.capstone.pickIt.api.project.dto.response.CompletionRequestResponseDTO;
import com.capstone.pickIt.domain.project.entity.ProjectTeamCompletionApproval;
import com.capstone.pickIt.domain.project.entity.ProjectTeamCompletionRequest;
import com.capstone.pickIt.domain.user.entity.User;

import java.util.List;

public class ProjectCompletionConverter {

    private ProjectCompletionConverter() {
    }

    public static CompletionRequestResponseDTO toResponse(
            ProjectTeamCompletionRequest request,
            List<ProjectTeamCompletionApproval> approvals
    ) {
        return new CompletionRequestResponseDTO(
                request.getId(),
                request.getProjectTeam().getId(),
                toUserSummary(request.getRequester()),
                request.getStatus(),
                request.getRequestedAt(),
                request.getFinalizedAt(),
                approvals.stream()
                        .map(ProjectCompletionConverter::toApprovalResponse)
                        .toList()
        );
    }

    private static CompletionRequestResponseDTO.CompletionApprovalResponseDTO toApprovalResponse(
            ProjectTeamCompletionApproval approval
    ) {
        return new CompletionRequestResponseDTO.CompletionApprovalResponseDTO(
                approval.getId(),
                toUserSummary(approval.getUser()),
                approval.getDecision(),
                approval.getDecidedAt()
        );
    }

    private static CompletionRequestResponseDTO.UserSummaryDTO toUserSummary(User user) {
        return new CompletionRequestResponseDTO.UserSummaryDTO(
                user.getId(),
                user.getNickname(),
                user.getMajor()
        );
    }
}