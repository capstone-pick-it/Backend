package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("""
        SELECT cr
        FROM ChatRoom cr
        WHERE cr.chatType = 'DIRECT'
          AND cr.directUserMinId = :minUserId
          AND cr.directUserMaxId = :maxUserId
        """)
    Optional<ChatRoom> findDirectChatRoomByUserIds(
            Long minUserId,
            Long maxUserId
    );

    @Query("""
        SELECT cr
        FROM ChatRoom cr
        WHERE cr.chatType = 'GROUP'
          AND cr.projectTeam.id = :projectTeamId
        """)
    Optional<ChatRoom> findGroupChatRoomByProjectTeamId(@Param("projectTeamId") Long projectTeamId);
}
