package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
        SELECT COUNT(m)
        FROM Message m
        WHERE m.chatRoom.id = :chatRoomId
        AND m.user.id <> :currentUserId
        AND (
          :lastReadMessageId IS NULL
          OR m.id > :lastReadMessageId
        )
    """)
    long countUnreadMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("currentUserId") Long currentUserId,
            @Param("lastReadMessageId") Long lastReadMessageId
    );
}
