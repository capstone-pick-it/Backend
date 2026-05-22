package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.PeerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PeerReviewRepository extends JpaRepository<PeerReview, Long> {

    @Query("SELECT COALESCE(AVG(pr.averageScore), 0) FROM PeerReview pr WHERE pr.reviewee.id = :userId")
    BigDecimal findAverageScoreByRevieweeId(@Param("userId") Long userId);
}