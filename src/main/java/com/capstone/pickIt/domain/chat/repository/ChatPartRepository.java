package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.ChatPart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
          :cursor IS NULL
          OR cr.lastMessageAt < :cursorLastMessageAt
          OR (cr.lastMessageAt = :cursorLastMessageAt AND cr.id < :cursor)
        )
        ORDER BY cr.lastMessageAt DESC, cr.id DESC
    """)
    List<ChatPart> findMyChatRooms(
            @Param("userId") Long userId,
            @Param("cursor") Long cursor,
            @Param("cursorLastMessageAt") LocalDateTime cursorLastMessageAt,
            Pageable pageable
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
        AND cp.deletedAt IS NULL
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
}
