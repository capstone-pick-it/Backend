package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.response.ProjectHistoryDetailResponseDTO;
import com.capstone.pickIt.api.user.dto.response.ProjectHistorySummaryResponseDTO;
import com.capstone.pickIt.domain.project.entity.ProjectTeam;
import com.capstone.pickIt.domain.project.entity.ProjectTeamMember;
import com.capstone.pickIt.domain.project.entity.ProjectTeamStatus;
import com.capstone.pickIt.domain.project.repository.PeerReviewRepository;
import com.capstone.pickIt.domain.project.repository.ProjectTeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageProjectHistoryService {

    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final PeerReviewRepository peerReviewRepository;

    @Transactional(readOnly = true)
    public ProjectHistorySummaryResponseDTO getProjectHistorySummary(Long userId) {
        long totalProjectCount = projectTeamMemberRepository.countConfirmedByUserId(userId);

        double averageScore = 0.0;
        long doneProjectCount = projectTeamMemberRepository.countDoneConfirmedByUserId(userId);

        if (totalProjectCount > 0) {
            averageScore = (double) doneProjectCount / totalProjectCount * 100;
        }

        double averageContribution = peerReviewRepository
                .findAverageScoreByRevieweeId(userId)
                .doubleValue();

        return ProjectHistorySummaryResponseDTO.builder()
                .projectCount((int) doneProjectCount) // DONE 프로젝트만 반환
                .averageScore(averageScore)
                .averageContribution(averageContribution)
                .build();
    }

    @Transactional(readOnly = true)
    public ProjectHistoryDetailResponseDTO getProjectHistoryDetail(Long userId) {
        List<ProjectTeamMember> members = projectTeamMemberRepository
                .findActiveConfirmedMembershipsWithTeamAndCourse(
                        userId, ProjectTeamStatus.DONE);

        double averageCompletionRate = members.stream()
                .mapToDouble(m -> m.getProjectTeam().getProgressRate() != null
                        ? m.getProjectTeam().getProgressRate().doubleValue() : 0.0)
                .average()
                .orElse(0.0);

        double averagePeerEvaluation = peerReviewRepository
                .findAverageScoreByRevieweeId(userId)
                .doubleValue();

        Map<Long, double[]> reviewAvgMap = peerReviewRepository
                .findAverageScoresByRevieweeGroupByTeam(userId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> new double[]{
                                row[1] != null ? ((Number) row[1]).doubleValue() : 0.0,
                                row[2] != null ? ((Number) row[2]).doubleValue() : 0.0,
                                row[3] != null ? ((Number) row[3]).doubleValue() : 0.0
                        }
                ));

        List<ProjectHistoryDetailResponseDTO.ProjectInfo> projects = members.stream()
                .map(member -> {
                    ProjectTeam team = member.getProjectTeam();
                    double[] avgs = reviewAvgMap.getOrDefault(team.getId(), new double[]{0.0, 0.0, 0.0});

                    return ProjectHistoryDetailResponseDTO.ProjectInfo.builder()
                            .projectName(team.getCourse().getCourseName())
                            .completionRate(team.getProgressRate() != null
                                    ? team.getProgressRate().doubleValue() : 0.0)
                            .teamEvaluation(ProjectHistoryDetailResponseDTO.TeamEvaluation.builder()
                                    .completion(avgs[0])
                                    .activeness(avgs[1])
                                    .teamSatisfaction(avgs[2])
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