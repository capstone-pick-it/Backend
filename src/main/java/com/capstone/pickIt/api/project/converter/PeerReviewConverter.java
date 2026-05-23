package com.capstone.pickIt.api.project.converter;

import com.capstone.pickIt.api.project.dto.response.PeerReviewResponseDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewStatusResponseDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewTargetListResponseDTO;
import com.capstone.pickIt.domain.project.entity.PeerReview;
import com.capstone.pickIt.domain.project.entity.ProjectTeamMember;
import com.capstone.pickIt.domain.project.entity.ProjectTeamReviewStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class PeerReviewConverter {

    private PeerReviewConverter() {
    }

    public static PeerReviewTargetListResponseDTO toTargetListResponse(
            Long projectTeamId,
            List<ProjectTeamMember> members,
            Set<Long> reviewedUserIds
    ) {
        return new PeerReviewTargetListResponseDTO(
                projectTeamId,
                members.stream()
                        .map(member ->
                                new PeerReviewTargetListResponseDTO.PeerReviewTargetResponseDTO(
                                        member.getUser().getId(),
                                        member.getUser().getNickname(),
                                        member.getUser().getMajor(),
                                        reviewedUserIds.contains(member.getUser().getId())
                                )
                        )
                        .toList()
        );
    }

    public static PeerReviewResponseDTO toResponse(
            PeerReview review
    ) {
        BigDecimal average =
                BigDecimal.valueOf(
                        review.getCompletionScore()
                                + review.getProactivityScore()
                                + review.getSatisfactionScore()
                ).divide(BigDecimal.valueOf(3));

        return new PeerReviewResponseDTO(
                review.getId(),
                review.getProjectTeam().getId(),
                review.getReviewer().getId(),
                review.getReviewee().getId(),
                review.getCompletionScore(),
                review.getProactivityScore(),
                review.getSatisfactionScore(),
                average,
                review.getCreatedAt()
        );
    }

    public static PeerReviewStatusResponseDTO toStatusResponse(
            Long projectTeamId,
            int requiredCount,
            int submittedCount,
            boolean isCompleted,
            List<ProjectTeamMember> targets,
            Set<Long> submittedRevieweeIds
    ) {
        return new PeerReviewStatusResponseDTO(
                projectTeamId,
                new PeerReviewStatusResponseDTO.MyReviewStatusDTO(
                        requiredCount,
                        submittedCount,
                        isCompleted
                ),
                targets.stream()
                        .map(target -> new PeerReviewStatusResponseDTO.PeerReviewStatusTargetDTO(
                                target.getUser().getId(),
                                target.getUser().getNickname(),
                                submittedRevieweeIds.contains(target.getUser().getId())
                        ))
                        .toList()
        );
    }
}