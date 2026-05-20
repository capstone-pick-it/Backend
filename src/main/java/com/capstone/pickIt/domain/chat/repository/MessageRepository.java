package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
