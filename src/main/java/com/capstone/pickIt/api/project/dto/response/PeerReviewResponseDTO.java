package com.capstone.pickIt.api.project.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PeerReviewResponseDTO(
        Long peerReviewId,
        Long projectTeamId,
        Long reviewerUserId,
        Long revieweeUserId,
        Integer completionScore,
        Integer proactivityScore,
        Integer satisfactionScore,
        BigDecimal averageScore,
        LocalDateTime submittedAt
) {
}