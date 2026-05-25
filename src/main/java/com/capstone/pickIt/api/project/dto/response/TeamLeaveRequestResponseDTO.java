package com.capstone.pickIt.api.project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamLeaveRequestResponseDTO {

    private Long teamLeaveRequestId;
    private Long projectTeamId;
    private Long requesterId;
    private String status;  // PENDING
}
