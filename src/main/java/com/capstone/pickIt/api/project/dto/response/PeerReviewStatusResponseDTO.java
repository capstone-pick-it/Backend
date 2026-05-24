package com.capstone.pickIt.api.project.dto.response;

import java.util.List;

public record PeerReviewStatusResponseDTO(
        Long projectTeamId,
        MyReviewStatusDTO myReviewStatus,
        List<PeerReviewStatusTargetDTO> targets
) {
    public record MyReviewStatusDTO(
            Integer requiredCount,
            Integer submittedCount,
            Boolean isCompleted
    ) {
    }

    public record PeerReviewStatusTargetDTO(
            Long revieweeUserId,
            String name,
            Boolean isSubmitted
    ) {
    }
}