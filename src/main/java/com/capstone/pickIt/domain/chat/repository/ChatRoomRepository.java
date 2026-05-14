package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("""
        SELECT cr
        FROM ChatRoom cr
        JOIN ChatPart cp1 ON cp1.chatRoom = cr
        JOIN ChatPart cp2 ON cp2.chatRoom = cr
        WHERE cr.chatType = 'DIRECT'
          AND cp1.user.id = :currentUserId
          AND cp2.user.id = :targetUserId
        """)
    Optional<ChatRoom> findDirectChatRoomByUserIds(
            Long currentUserId,
            Long targetUserId
    );
}
