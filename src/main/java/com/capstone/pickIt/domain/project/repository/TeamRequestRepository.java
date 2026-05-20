package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.TeamRequest;
import com.capstone.pickIt.domain.project.entity.TeamRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    boolean existsByChatRoomIdAndReceiverIdAndTeamRequestStatus(
            Long chatRoomId,
            Long receiverId,
            TeamRequestStatus teamRequestStatus
    );

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE TeamRequest tr
        SET tr.teamRequestStatus = 'REJECTED',
            tr.respondedAt = :now,
            tr.pendingUniqueFlag = null
        WHERE tr.teamRequestStatus = 'PENDING'
          AND tr.createdAt <= :expiredBefore
        """)
    int rejectExpiredPendingRequests(
            LocalDateTime expiredBefore,
            LocalDateTime now
    );
}
