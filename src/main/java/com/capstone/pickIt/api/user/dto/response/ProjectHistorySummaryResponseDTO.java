package com.capstone.pickIt.api.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "프로젝트 이력 요약 응답")
public class ProjectHistorySummaryResponseDTO {

    @Schema(description = "프로젝트 참여 수", example = "3")
    private int projectCount;

    @Schema(description = "완수율 (%)", example = "100.0")
    private double averageScore;

    @Schema(description = "상호 평가 평균 점수 (1~5)", example = "4.5")
    private double averageContribution;
}