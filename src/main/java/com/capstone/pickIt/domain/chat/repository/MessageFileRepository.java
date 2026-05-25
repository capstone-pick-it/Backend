package com.capstone.pickIt.domain.chat.repository;

import com.capstone.pickIt.domain.chat.entity.MessageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageFileRepository extends JpaRepository<MessageFile, Long> {

    List<MessageFile> findByMessageIdIn(List<Long> messageIds);
}
