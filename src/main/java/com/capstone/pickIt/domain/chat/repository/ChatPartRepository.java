package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.ChatPart;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatPartRepository extends JpaRepository<ChatPart, Long> {
    Optional<ChatPart> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    @Query("""
            SELECT cp
            FROM ChatPart cp
            JOIN FETCH cp.user
            WHERE cp.chatRoom.id = :chatRoomId
              AND cp.user.id <> :currentUserId
              AND cp.deletedAt IS NULL
            """)
    Optional<ChatPart> findActiveOpponent(
            Long chatRoomId,
            Long currentUserId
    );

    @Query("""
            SELECT cp
            FROM ChatPart cp
            JOIN FETCH cp.user
            WHERE cp.chatRoom.id = :chatRoomId
              AND cp.user.id <> :currentUserId
            """)
    Optional<ChatPart> findOpponent(
            Long chatRoomId,
            Long currentUserId
    );

    @Query("""
            SELECT COUNT(cp)
            FROM ChatPart cp
            WHERE cp.chatRoom.id = :chatRoomId
              AND cp.deletedAt IS NULL
            """)
    int countActiveParticipants(Long chatRoomId);

    @Query("""
        SELECT cp
        FROM ChatPart cp
        JOIN FETCH cp.chatRoom cr
        LEFT JOIN FETCH cr.lastMessage lm
        WHERE cp.user.id = :userId
        AND cp.deletedAt IS NULL
        AND (
            :cursorChatRoomId IS NULL
            OR (
                COALESCE(cr.lastMessageAt, cr.createdAt) < :cursorSortAt
                OR (
                    COALESCE(cr.lastMessageAt, cr.createdAt) = :cursorSortAt
                    AND cr.id < :cursorChatRoomId
                )
            )
        )
        ORDER BY
            COALESCE(cr.lastMessageAt, cr.createdAt) DESC,
            cr.id DESC
    """)
    List<ChatPart> findMyChatRooms(
            @Param("userId") Long userId,
            @Param("cursorSortAt") LocalDateTime cursorSortAt,
            @Param("cursorChatRoomId") Long cursorChatRoomId,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT cp
        FROM ChatPart cp
        JOIN FETCH cp.chatRoom cr
        LEFT JOIN FETCH cr.projectTeam
        WHERE cr.id = :chatRoomId
          AND cp.user.id = :userId
        """)
    Optional<ChatPart> findByChatRoomIdAndUserIdWithLock(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId
    );

    public interface ParticipantCountProjection {
        Long getChatRoomId();
        Long getParticipantCount();
    }

    @Query("""
        SELECT cp.chatRoom.id AS chatRoomId,
               COUNT(cp) AS participantCount
        FROM ChatPart cp
        WHERE cp.chatRoom.id IN :chatRoomIds
        AND cp.deletedAt IS NULL
        GROUP BY cp.chatRoom.id
    """)
    List<ParticipantCountProjection> countParticipantsByChatRoomIds(
            @Param("chatRoomIds") List<Long> chatRoomIds
    );

    public interface OpponentProjection {
        Long getChatRoomId();
        Long getUserId();
        String getNickname();
    }

    @Query("""
        SELECT cp.chatRoom.id AS chatRoomId,
               cp.user.id AS userId,
               cp.user.nickname AS nickname
        FROM ChatPart cp
        WHERE cp.chatRoom.id IN :chatRoomIds
        AND cp.user.id <> :currentUserId
    """)
    List<OpponentProjection> findOpponentsByChatRoomIds(
            @Param("chatRoomIds") List<Long> chatRoomIds,
            @Param("currentUserId") Long currentUserId
    );

    public interface UnreadMemberCountProjection {
        Long getMessageId();
        Long getUnreadMemberCount();
    }

    @Query("""
        SELECT m.id AS messageId,
                COUNT(cp) AS unreadMemberCount
        FROM Message m
        JOIN ChatPart cp
        ON cp.chatRoom.id=m.chatRoom.id
        WHERE m.chatRoom.id = :chatRoomId
        AND m.id IN :messageIds
        AND (
            cp.lastReadMessage IS NULL
            OR cp.lastReadMessage.id < m.id
        )
        AND cp.deletedAt IS NULL
        GROUP BY m.id
    """)
    List<UnreadMemberCountProjection> countUnreadMemberByMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("messageIds") List<Long> messageIds
    );

    @Query("""
        SELECT cp
        FROM ChatPart cp
        JOIN FETCH cp.user
        WHERE cp.chatRoom.id = :chatRoomId
            AND cp.deletedAt IS NULL
    """)
    List<ChatPart> findActiveParticipantsWithUserByChatRoomId(
            @Param("chatRoomId") Long chatRoomId
    );
}
