package com.capstone.pickIt.api.teamlevel.service;

import com.capstone.pickIt.domain.point.entity.PointTransactionType;
import com.capstone.pickIt.domain.point.repository.PointTransactionRepository;
import com.capstone.pickIt.domain.project.repository.PeerReviewRepository;
import com.capstone.pickIt.domain.project.repository.ProjectTeamMemberRepository;
import com.capstone.pickIt.domain.teamlevel.entity.TeamLevel;
import com.capstone.pickIt.domain.teamlevel.repository.TeamLevelRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.exception.UserErrorCode;
import com.capstone.pickIt.domain.user.exception.UserException;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.capstone.pickIt.domain.teamlevel.policy.TeamLevelPolicy.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamLevelServiceImpl implements TeamLevelService {

    private final TeamLevelRepository teamLevelRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final PeerReviewRepository peerReviewRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final UserRepository userRepository;

    @Override
    public void recalculate(Long userId) {
        long completedCount = projectTeamMemberRepository.countDoneConfirmedByUserId(userId);
        long totalCount = projectTeamMemberRepository.countConfirmedByUserId(userId);
        BigDecimal avgReviewScore = peerReviewRepository.findAverageScoreByRevieweeId(userId);
        long penaltyCount = pointTransactionRepository.countByUserIdAndTransactionType(
                userId, PointTransactionType.PROJECT_PENALTY
        );

        int score = calculateScore(completedCount, totalCount, avgReviewScore, penaltyCount);
        int level = convertToLevel(score);

        TeamLevel teamLevel = teamLevelRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
                    return teamLevelRepository.save(TeamLevel.builder().user(user).build());
                });

        teamLevel.updateLevel(level);
    }

    // ── 점수 계산 ─────────────────────────────────────────────────────────────

    private int calculateScore(
            long completedCount,
            long totalCount,
            BigDecimal avgReviewScore,
            long penaltyCount
    ) {
        int baseScore = projectCountScore(completedCount)
                + completionRateScore(completedCount, totalCount)
                + peerReviewScore(avgReviewScore);

        int deduction = penaltyDeduction(penaltyCount)
                + lowCompletionRateDeduction(completedCount, totalCount)
                + lowPeerReviewDeduction(avgReviewScore);

        return Math.max(baseScore - deduction, 0);
    }

    // 참여 프로젝트 수 점수 (max 30)
    private int projectCountScore(long completedCount) {
        return (int) Math.min(completedCount * SCORE_PER_PROJECT, MAX_PROJECT_COUNT_SCORE);
    }

    // 완수율 점수 (max 40)
    private int completionRateScore(long completedCount, long totalCount) {
        if (totalCount == 0) return 0;
        return (int) (completedCount * MAX_COMPLETION_RATE_SCORE / totalCount);
    }

    // 상호평가 점수 (max 30): avgScore 1.0~5.0 → 0~30
    private int peerReviewScore(BigDecimal avgReviewScore) {
        if (avgReviewScore.compareTo(BigDecimal.ZERO) <= 0) return 0;
        return avgReviewScore
                .subtract(BigDecimal.ONE)
                .divide(BigDecimal.valueOf(4), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(MAX_PEER_REVIEW_SCORE))
                .intValue();
    }

    // 패널티 감점: 1회당 -5점, max -30점
    private int penaltyDeduction(long penaltyCount) {
        return (int) Math.min(penaltyCount * DEDUCTION_PER_PENALTY, MAX_PENALTY_DEDUCTION);
    }

    // 완수율 낮음 감점: 완수율 50% 미만 시 -10점
    private int lowCompletionRateDeduction(long completedCount, long totalCount) {
        if (totalCount == 0) return 0;
        double completionRate = (double) completedCount / totalCount;
        return completionRate < LOW_COMPLETION_RATE_THRESHOLD ? LOW_COMPLETION_RATE_DEDUCTION : 0;
    }

    // 상호평가 낮음 감점: 평균 2.0 미만 시 -10점
    private int lowPeerReviewDeduction(BigDecimal avgReviewScore) {
        if (avgReviewScore.compareTo(BigDecimal.ZERO) <= 0) return 0;
        return avgReviewScore.compareTo(LOW_PEER_REVIEW_THRESHOLD) < 0
                ? LOW_PEER_REVIEW_DEDUCTION
                : 0;
    }

    // ── 레벨 변환 ─────────────────────────────────────────────────────────────

    // score 0 → LV.0 / 1~20 → LV.1 / 21~40 → LV.2 / ... / 81~100 → LV.5
    private int convertToLevel(int score) {
        if (score <= 0) return 0;
        return Math.min((score - 1) / LEVEL_INTERVAL + 1, MAX_LEVEL);
    }
}