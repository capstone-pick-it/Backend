package com.capstone.pickIt.api.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "프로젝트 이력 상세 응답")
public class ProjectHistoryDetailResponseDTO {

    @Schema(description = "프로젝트 참여 수", example = "3")
    private int participationCount;

    @Schema(description = "전체 프로젝트 평균 완수율 (%)", example = "100.0")
    private double averageCompletionRate;

    @Schema(description = "전체 프로젝트 평균 상호평가 점수 (5점 만점)", example = "4.0")
    private double averagePeerEvaluation;

    @Schema(description = "프로젝트 목록")
    private List<ProjectInfo> projects;

    @Getter
    @Builder
    @Schema(description = "개별 프로젝트 정보")
    public static class ProjectInfo {

        @Schema(description = "프로젝트명 (강의명)", example = "소프트웨어분석및설계")
        private String projectName;

        @Schema(description = "해당 프로젝트 완수율 (%)", example = "100.0")
        private double completionRate;

        @Schema(description = "팀 평가 점수")
        private TeamEvaluation teamEvaluation;
    }

    @Getter
    @Builder
    @Schema(description = "팀 평가 항목")
    public static class TeamEvaluation {

        @Schema(description = "역할 수행 평균 점수", example = "4.0")
        private double completion;

        @Schema(description = "적극성 평균 점수", example = "4.0")
        private double activeness;

        @Schema(description = "협업 만족도 평균 점수", example = "4.0")
        private double teamSatisfaction;
    }
}