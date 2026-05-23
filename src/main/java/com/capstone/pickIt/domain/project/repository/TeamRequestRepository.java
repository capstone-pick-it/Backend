package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.TeamRequest;
import com.capstone.pickIt.domain.project.entity.TeamRequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

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

    boolean existsByChatRoomIdAndReceiverIdAndTeamRequestStatus(
            Long chatRoomId,
            Long receiverId,
            TeamRequestStatus teamRequestStatus
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT tr FROM TeamRequest tr WHERE tr.id = :teamRequestId")
    Optional<TeamRequest> findByIdWithLock(@Param("teamRequestId") Long teamRequestId);
}
