package com.capstone.pickIt.domain.point.repository;

import com.capstone.pickIt.domain.point.entity.PointTransaction;
import com.capstone.pickIt.domain.point.entity.PointTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    Optional<PointTransaction> findTopByUserIdAndTransactionTypeOrderByCreatedAtDesc(
            Long userId,
            PointTransactionType transactionType
    );
}