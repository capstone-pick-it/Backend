package com.capstone.pickIt.api.project.dto.response;

import java.util.List;

public record PeerReviewTargetListResponseDTO(
        Long projectTeamId,
        List<PeerReviewTargetResponseDTO> targets
) {
    public record PeerReviewTargetResponseDTO(
            Long userId,
            String nickname,
            String major,
            boolean reviewed
    ) {
    }
}