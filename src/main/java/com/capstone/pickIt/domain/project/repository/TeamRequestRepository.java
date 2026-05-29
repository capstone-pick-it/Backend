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
import java.util.List;
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

    @Query("""
        SELECT COUNT(tr) > 0
        FROM TeamRequest tr
        WHERE tr.teamRequestStatus = :status
        AND (
            (tr.sender.id = :userId AND tr.receiver.id = :opponentUserId)
            OR
            (tr.sender.id = :opponentUserId AND tr.receiver.id = :userId)
        )
    """)
    boolean existsPendingBetweenUsers(
            @Param("userId") Long userId,
            @Param("opponentUserId") Long opponentUserId,
            @Param("status") TeamRequestStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT tr FROM TeamRequest tr WHERE tr.id = :teamRequestId")
    Optional<TeamRequest> findByIdWithLock(@Param("teamRequestId") Long teamRequestId);

    @Query("""
        SELECT tr.chatRoom.id
        FROM TeamRequest tr
        WHERE tr.chatRoom.id IN :chatRoomIds
        AND tr.receiver.id = :currentUserId
        AND tr.teamRequestStatus = :status
    """)
    List<Long> findPendingRequestChatRoomIds(
            @Param("chatRoomIds") List<Long> chatRoomIds,
            @Param("currentUserId") Long currentUserId,
            @Param("status") TeamRequestStatus status
    );

    @Query("""
        SELECT tr
        FROM TeamRequest tr
        JOIN FETCH tr.sender
        JOIN FETCH tr.receiver
        WHERE tr.chatRoom.id = :chatRoomId
        ORDER BY tr.createdAt DESC, tr.id DESC
    """)
    List<TeamRequest> findLatestByChatRoomId(
            @Param("chatRoomId") Long chatRoomId,
            Pageable pageable
    );

    @Query("""
        SELECT tr
        FROM TeamRequest tr
        JOIN FETCH tr.sender
        JOIN FETCH tr.receiver
        JOIN FETCH tr.course
        WHERE tr.chatRoom.id = :chatRoomId
        ORDER BY tr.createdAt DESC, tr.id DESC
    """)
    List<TeamRequest> findLatestByChatRoomIdWithCourse(
            @Param("chatRoomId") Long chatRoomId,
            Pageable pageable
    );
}
