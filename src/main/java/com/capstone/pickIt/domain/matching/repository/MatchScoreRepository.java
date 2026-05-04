package com.capstone.pickIt.domain.matching.repository;

import com.capstone.pickIt.domain.matching.entity.MatchScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchScoreRepository extends JpaRepository<MatchScore, Long> {
}