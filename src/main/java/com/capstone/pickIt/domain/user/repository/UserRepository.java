package com.capstone.pickIt.domain.user.repository;

import com.capstone.pickIt.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    // 탈퇴 후 30일 지난 사용자 조회 (하드 딜리트용)
    List<User> findByDeletedAtBeforeAndDeletedAtIsNotNull(LocalDateTime dateTime);
}