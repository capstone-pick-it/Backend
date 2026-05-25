package com.capstone.pickIt.api.project.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeamLeaveRequestResponseDTO {

    private Long teamLeaveRequestId;
    private Long projectTeamId;
    private Long requesterId;
    private String requesterNickname;
    private String status;
    private long approvedCount;   // 현재 인정한 팀원 수
    private long requiredCount;   // 인정 필요한 전체 팀원 수 (요청자 제외)
    private LocalDateTime createdAt;
}
