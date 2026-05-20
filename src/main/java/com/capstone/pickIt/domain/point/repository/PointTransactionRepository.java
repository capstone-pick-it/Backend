package com.capstone.pickIt.domain.point.repository;

import com.capstone.pickIt.domain.point.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
}