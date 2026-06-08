package com.capstone.pickIt.domain.matching.repository;

import com.capstone.pickIt.domain.matching.entity.MatchScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchScoreRepository extends JpaRepository<MatchScore, Long> {

    // 특정 유저가 특정 카드에 대해 계산한 점수 삭제
    @Modifying
    @Query("DELETE FROM MatchScore ms WHERE ms.user.id = :userId AND ms.userCourseProfile.id = :profileId")
    void deleteByUserIdAndProfileId(@Param("userId") Long userId, @Param("profileId") Long profileId);

    // 특정 카드에 대한 모든 점수 삭제 (카드 삭제 시)
    @Modifying
    @Query("DELETE FROM MatchScore ms WHERE ms.userCourseProfile.id = :profileId")
    void deleteByProfileId(@Param("profileId") Long profileId);

    // 특정 유저가 계산한 모든 점수 삭제 (성향 수정 시)
    @Modifying
    @Query("DELETE FROM MatchScore ms WHERE ms.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}