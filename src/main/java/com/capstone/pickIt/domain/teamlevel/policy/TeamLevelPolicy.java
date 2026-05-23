package com.capstone.pickIt.domain.teamlevel.policy;

import java.math.BigDecimal;

public final class TeamLevelPolicy {

    private TeamLevelPolicy() {}

    // ── 가점 요소 ──────────────────────────────────────
    // 참여 프로젝트 수 점수 (max 30)
    public static final int SCORE_PER_PROJECT = 10;
    public static final int MAX_PROJECT_COUNT_SCORE = 30;

    // 완수율 점수 (max 40)
    public static final int MAX_COMPLETION_RATE_SCORE = 40;

    // 상호평가 점수 (max 30)
    public static final int MAX_PEER_REVIEW_SCORE = 30;

    // ── 감점 요소 ──────────────────────────────────────
    // 패널티 이력: 1회당 -5점, 최대 -30점
    public static final int DEDUCTION_PER_PENALTY = 5;
    public static final int MAX_PENALTY_DEDUCTION = 30;

    // 완수율 낮음 기준: 50% 미만 시 -10점
    public static final double LOW_COMPLETION_RATE_THRESHOLD = 0.5;
    public static final int LOW_COMPLETION_RATE_DEDUCTION = 10;

    // 상호평가 낮음 기준: 평균 2.0 미만 시 -10점
    public static final BigDecimal LOW_PEER_REVIEW_THRESHOLD = BigDecimal.valueOf(2.0);
    public static final int LOW_PEER_REVIEW_DEDUCTION = 10;

    // ── 레벨 변환 ──────────────────────────────────────
    // 20점 단위, LV.0~5
    public static final int LEVEL_INTERVAL = 20;
    public static final int MAX_LEVEL = 5;
}