package com.capstone.pickIt.api.project.service;

import com.capstone.pickIt.api.project.dto.request.PeerReviewCreateRequestDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewResponseDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewStatusResponseDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewTargetListResponseDTO;

public interface PeerReviewService {

    PeerReviewTargetListResponseDTO getPeerReviewTargets(
            Long projectTeamId
    );

    PeerReviewResponseDTO createPeerReview(
            Long projectTeamId,
            PeerReviewCreateRequestDTO request
    );

    PeerReviewStatusResponseDTO getPeerReviewStatus(
            Long projectTeamId
    );
}