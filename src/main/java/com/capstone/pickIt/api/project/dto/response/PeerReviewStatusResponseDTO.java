package com.capstone.pickIt.api.project.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PeerReviewStatusResponseDTO(
        Long projectTeamId,
        List<MemberReviewStatusResponseDTO> members
) {
    public record MemberReviewStatusResponseDTO(
            Long userId,
            String nickname,
            Integer expectedReviewCount,
            Integer submittedReviewCount,
            Boolean completed,
            LocalDateTime completedAt
    ) {
    }
}