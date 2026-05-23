package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.ChatPart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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

    int countByChatRoomIdAndDeletedAtIsNull(Long chatRoomId);
}
