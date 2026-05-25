package com.capstone.pickIt.api.project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConfirmResponseDTO {

    private Long projectTeamId;
    private String myRecruitmentConfirmStatus;  // CONFIRMED
    private boolean allConfirmed;               // 전원 확정 여부
    private String projectTeamStatus;           // RECRUITING or IN_PROGRESS
}
