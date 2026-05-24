package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
        AND cp.deletedAt IS NULL
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

    @Query("""
        SELECT COUNT(m)
        FROM Message m
        JOIN ChatPart cp ON cp.chatRoom.id = m.chatRoom.id
        WHERE m.chatRoom.id = :chatRoomId
            AND cp.user.id = :userId
            AND cp.deletedAt IS NULL
            AND m.user.id <> :userId
            AND (
                cp.lastReadMessage IS NULL
                OR m.id > cp.lastReadMessage.id
            )
    """)
    Long countUnreadMessagesByChatRoomId(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT m
        FROM Message m
        JOIN FETCH m.user
        WHERE m.chatRoom.id=:chatRoomId
        AND (
            :cursor IS NULL
            OR m.id < :cursor
        )
        ORDER BY m.id DESC
    """)
    List<Message> findMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    @Query("""
        SELECT m
        FROM Message m
        WHERE m.id = :messageId
            AND m.chatRoom.id = :chatRoomId
    """)
    Optional<Message> findByIdAndChatRoomId(
            @Param("messageId") Long messageId,
            @Param("chatRoomId") Long chatRoomId
    );

}
