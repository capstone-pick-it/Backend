package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    public interface UnreadCountProjection {
        Long getChatRoomId();
        Long getUnreadCount();
    }

    @Query("""
        SELECT m.chatRoom.id AS chatRoomId,
               COUNT(m) AS unreadCount
        FROM Message m
        JOIN ChatPart cp
        ON cp.chatRoom.id = m.chatRoom.id
        AND cp.user.id = :currentUserId
        WHERE m.chatRoom.id IN :chatRoomIds
        AND m.user.id <> :currentUserId
        AND (
            cp.lastReadMessage IS NULL
            OR m.id > cp.lastReadMessage.id
        )
        GROUP BY m.chatRoom.id
    """)
    List<UnreadCountProjection> countUnreadMessagesByChatRoomIds(
            @Param("chatRoomIds") List<Long> chatRoomIds,
            @Param("currentUserId") Long currentUserId
    );
}
