package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.TeamRequest;
import com.capstone.pickIt.domain.project.entity.TeamRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TeamRequestRepository extends JpaRepository<TeamRequest, Long> {

    boolean existsBySenderIdAndCourseIdAndTeamRequestStatus(
            Long senderId,
            Long courseId,
            TeamRequestStatus teamRequestStatus
    );

    boolean existsBySenderIdAndReceiverIdAndTeamRequestStatus(
            Long senderId,
            Long receiverId,
            TeamRequestStatus teamRequestStatus
    );

    Page<TeamRequest> findByTeamRequestStatusAndCreatedAtLessThanEqual(
            TeamRequestStatus teamRequestStatus,
            LocalDateTime createdAt,
            Pageable pageable
    );
}
