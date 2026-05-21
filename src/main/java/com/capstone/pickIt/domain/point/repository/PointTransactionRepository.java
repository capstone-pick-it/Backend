package com.capstone.pickIt.domain.point.repository;

import com.capstone.pickIt.domain.point.entity.PointTransaction;
import com.capstone.pickIt.domain.point.entity.PointTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PointTransaction pt WHERE pt.user.id IN :userIds")
    void deleteAllByUserIdIn(@Param("userIds") List<Long> userIds);
    Optional<PointTransaction> findTopByUserIdAndTransactionTypeOrderByCreatedAtDesc(
            Long userId,
            PointTransactionType transactionType
    );
}