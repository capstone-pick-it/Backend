package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.response.ProjectHistoryDetailResponseDTO;
import com.capstone.pickIt.api.user.dto.response.ProjectHistorySummaryResponseDTO;
import com.capstone.pickIt.domain.project.entity.PeerReview;
import com.capstone.pickIt.domain.project.entity.ProjectTeam;
import com.capstone.pickIt.domain.project.entity.ProjectTeamMember;
import com.capstone.pickIt.domain.project.entity.RecruitmentConfirmStatus;
import com.capstone.pickIt.domain.project.repository.PeerReviewRepository;
import com.capstone.pickIt.domain.project.repository.ProjectTeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public ProjectHistoryDetailResponseDTO getProjectHistoryDetail(Long userId) {
        List<ProjectTeamMember> members = projectTeamMemberRepository
                .findAllByUserIdAndRecruitmentConfirmStatusAndLeftAtIsNullOrderByJoinedAtDesc(
                        userId, RecruitmentConfirmStatus.CONFIRMED);

        double averageCompletionRate = members.stream()
                .mapToDouble(m -> m.getProjectTeam().getProgressRate() != null
                        ? m.getProjectTeam().getProgressRate().doubleValue() : 0.0)
                .average()
                .orElse(0.0);

        double averagePeerEvaluation = peerReviewRepository
                .findAverageScoreByRevieweeId(userId)
                .doubleValue();

        List<ProjectHistoryDetailResponseDTO.ProjectInfo> projects = members.stream()
                .map(member -> {
                    ProjectTeam team = member.getProjectTeam();
                    List<PeerReview> reviews = peerReviewRepository
                            .findByProjectTeamIdAndRevieweeId(team.getId(), userId);

                    double avgCompletion = reviews.stream()
                            .mapToInt(PeerReview::getCompletionScore).average().orElse(0.0);
                    double avgActiveness = reviews.stream()
                            .mapToInt(PeerReview::getProactivityScore).average().orElse(0.0);
                    double avgSatisfaction = reviews.stream()
                            .mapToInt(PeerReview::getSatisfactionScore).average().orElse(0.0);

                    return ProjectHistoryDetailResponseDTO.ProjectInfo.builder()
                            .projectName(team.getCourse().getCourseName())
                            .completionRate(team.getProgressRate() != null
                                    ? team.getProgressRate().doubleValue() : 0.0)
                            .teamEvaluation(ProjectHistoryDetailResponseDTO.TeamEvaluation.builder()
                                    .completion(avgCompletion)
                                    .activeness(avgActiveness)
                                    .teamSatisfaction(avgSatisfaction)
                                    .build())
                            .build();
                })
                .toList();

        return ProjectHistoryDetailResponseDTO.builder()
                .participationCount(members.size())
                .averageCompletionRate(averageCompletionRate)
                .averagePeerEvaluation(averagePeerEvaluation)
                .projects(projects)
                .build();
    }
}