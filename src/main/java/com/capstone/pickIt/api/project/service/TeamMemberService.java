package com.capstone.pickIt.api.project.service;

import com.capstone.pickIt.api.project.dto.response.ConfirmResponseDTO;
import com.capstone.pickIt.api.project.dto.response.TeamLeaveRequestResponseDTO;

public interface TeamMemberService {

    // 팀원 확정 (PENDING → CONFIRMED, 전원 확정 시 팀 IN_PROGRESS)
    ConfirmResponseDTO confirm(Long projectTeamId);

    // 팀 나가기 - PENDING 상태 (패널티 없음, Case 1)
    void leavePending(Long projectTeamId);

    // 나가기 요청 생성 - CONFIRMED/IN_PROGRESS (Case 3-1)
    TeamLeaveRequestResponseDTO requestLeave(Long projectTeamId);

    // 나가기 인정 - 다른 팀원이 나가기 요청에 동의 (Case 3-1)
    void approveLeave(Long projectTeamId);

    /** 강제 나가기 - CONFIRMED/IN_PROGRESS (포인트 감점, Case 3-2) */
    void leaveForce(Long projectTeamId);
}
