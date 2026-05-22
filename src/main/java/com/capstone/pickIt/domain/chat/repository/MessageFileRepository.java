package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.MessageFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageFileRepository extends JpaRepository<MessageFile, Long> {
}
