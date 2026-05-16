package com.capstone.pickIt.api.point.dto.response;

import java.time.LocalDateTime;

public record PointResponseDTO(
        Long userId,
        Integer balance,
        Boolean recoveryApplied,
        Integer recoveredPoint,
        LocalDateTime lastRecoveredAt
) {
}