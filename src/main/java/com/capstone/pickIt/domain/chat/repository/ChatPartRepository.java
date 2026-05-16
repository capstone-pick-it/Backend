package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.ChatPart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatPartRepository extends JpaRepository<ChatPart, Long> {
    Optional<ChatPart> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);
}
