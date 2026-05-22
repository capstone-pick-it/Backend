package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.response.ProjectHistorySummaryResponseDTO;
import com.capstone.pickIt.domain.project.repository.PeerReviewRepository;
import com.capstone.pickIt.domain.project.repository.ProjectTeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MypageProjectHistoryService {

    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final PeerReviewRepository peerReviewRepository;

    @Transactional(readOnly = true)
    public ProjectHistorySummaryResponseDTO getProjectHistorySummary(Long userId) {
        long projectCount = projectTeamMemberRepository.countConfirmedByUserId(userId);

        double averageScore = 0.0;
        if (projectCount > 0) {
            long doneCount = projectTeamMemberRepository.countDoneConfirmedByUserId(userId);
            averageScore = (double) doneCount / projectCount * 100;
        }

        double averageContribution = peerReviewRepository
                .findAverageScoreByRevieweeId(userId)
                .doubleValue();

        return ProjectHistorySummaryResponseDTO.builder()
                .projectCount((int) projectCount)
                .averageScore(averageScore)
                .averageContribution(averageContribution)
                .build();
    }
}