package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.TeamLeaveApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamLeaveApprovalRepository extends JpaRepository<TeamLeaveApproval, Long> {

    List<TeamLeaveApproval> findByTeamLeaveRequestId(Long teamLeaveRequestId);

    boolean existsByTeamLeaveRequestIdAndApproverId(Long teamLeaveRequestId, Long approverId);

    long countByTeamLeaveRequestId(Long teamLeaveRequestId);
}
