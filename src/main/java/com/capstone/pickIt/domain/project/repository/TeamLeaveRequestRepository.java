package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.TeamLeaveRequest;
import com.capstone.pickIt.domain.project.entity.TeamLeaveRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamLeaveRequestRepository extends JpaRepository<TeamLeaveRequest, Long> {

    Optional<TeamLeaveRequest> findByProjectTeamIdAndStatus(Long projectTeamId, TeamLeaveRequestStatus status);

    boolean existsByProjectTeamIdAndStatus(Long projectTeamId, TeamLeaveRequestStatus status);
}
