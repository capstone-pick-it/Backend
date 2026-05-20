package com.capstone.pickIt.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.messaging.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
