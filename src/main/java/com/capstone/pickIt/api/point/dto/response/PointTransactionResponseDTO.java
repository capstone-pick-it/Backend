package com.capstone.pickIt.api.point.dto.response;

import com.capstone.pickIt.domain.point.entity.PointTransactionType;

import java.time.LocalDateTime;

public record PointTransactionResponseDTO(
        Long pointTransactionId,
        Long projectTeamId,
        PointTransactionType transactionType,
        Integer amount,
        Integer balanceAfter,
        String description,
        LocalDateTime createdAt
) {
}