package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.ChatPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
