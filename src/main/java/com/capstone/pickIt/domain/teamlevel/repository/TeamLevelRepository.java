package com.capstone.pickIt.domain.teamlevel.repository;

import com.capstone.pickIt.domain.teamlevel.entity.TeamLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamLevelRepository extends JpaRepository<TeamLevel, Long> {

    Optional<TeamLevel> findByUserId(Long userId);
}