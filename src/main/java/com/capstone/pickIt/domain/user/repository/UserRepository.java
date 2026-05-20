package com.capstone.pickIt.domain.user.repository;

import com.capstone.pickIt.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.email = :email")
    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

    // 탈퇴 후 30일 지난 사용자 조회 (하드 딜리트용)
    List<User> findByDeletedAtBeforeAndDeletedAtIsNotNull(LocalDateTime dateTime);
}