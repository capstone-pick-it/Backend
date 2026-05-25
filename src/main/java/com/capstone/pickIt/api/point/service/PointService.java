package com.capstone.pickIt.api.point.service;

import com.capstone.pickIt.api.point.dto.response.PointResponseDTO;
import com.capstone.pickIt.domain.point.entity.PointTransactionType;

public interface PointService {

    PointResponseDTO getMyPoint();

    PointResponseDTO refreshAndGetPoint(Long userId);

    void earnPoint(Long userId, int amount, PointTransactionType transactionType, String description);

    void usePoint(Long userId, int amount, PointTransactionType transactionType, String description);
}
