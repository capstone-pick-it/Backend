package com.capstone.pickIt.api.point.service;

import com.capstone.pickIt.api.point.dto.response.PointResponseDTO;
import com.capstone.pickIt.domain.point.entity.Point;
import com.capstone.pickIt.domain.point.entity.PointTransaction;
import com.capstone.pickIt.domain.point.entity.PointTransactionType;
import com.capstone.pickIt.domain.point.exception.PointErrorCode;
import com.capstone.pickIt.domain.point.exception.PointException;
import com.capstone.pickIt.domain.point.repository.PointRepository;
import com.capstone.pickIt.domain.point.repository.PointTransactionRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointServiceImpl implements PointService {

    public static final int PROJECT_REQUIRED_POINT = 20;
    private static final int WEEKLY_RECOVERY_POINT = 2;
    private static final int RECOVERY_INTERVAL_DAYS = 7;

    private final PointRepository pointRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PointResponseDTO getMyPoint() {
        Long currentUserId = SecurityUtil.requireUserId();
        return calculatePoint(currentUserId);
    }

    @Override
    @Transactional
    public PointResponseDTO calculatePoint(Long userId) {
        Point point = findPoint(userId);
        LocalDateTime lastRecoveredAt = findLastRecoveredAt(userId);

        boolean recoveryApplied = false;
        int recoveredPoint = 0;

        if (isRecoveryAvailable(point, lastRecoveredAt)) {
            recoveredPoint = Math.min(
                    WEEKLY_RECOVERY_POINT,
                    PROJECT_REQUIRED_POINT - point.getBalance()
            );

            point.add(recoveredPoint);
            saveRecoveryTransaction(userId, point, recoveredPoint);

            recoveryApplied = true;
            lastRecoveredAt = LocalDateTime.now();
        }

        return new PointResponseDTO(
                userId,
                point.getBalance(),
                recoveryApplied,
                recoveredPoint,
                lastRecoveredAt
        );
    }

    private boolean isRecoveryAvailable(Point point, LocalDateTime lastRecoveredAt) {
        if (point.getBalance() >= PROJECT_REQUIRED_POINT) {
            return false;
        }

        if (lastRecoveredAt == null) {
            return true;
        }

        return !lastRecoveredAt
                .plusDays(RECOVERY_INTERVAL_DAYS)
                .isAfter(LocalDateTime.now());
    }

    private void saveRecoveryTransaction(Long userId, Point point, int recoveredPoint) {
        PointTransaction transaction = PointTransaction.builder()
                .user(findUser(userId))
                .transactionType(PointTransactionType.WEEKLY_RECOVERY)
                .amount(recoveredPoint)
                .balanceAfter(point.getBalance())
                .description("주간 포인트 자동 회복")
                .build();

        pointTransactionRepository.save(transaction);
    }

    private LocalDateTime findLastRecoveredAt(Long userId) {
        return pointTransactionRepository
                .findTopByUserIdAndTransactionTypeOrderByCreatedAtDesc(
                        userId,
                        PointTransactionType.WEEKLY_RECOVERY
                )
                .map(PointTransaction::getCreatedAt)
                .orElse(null);
    }

    private Point findPoint(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.USER_NOT_FOUND));
    }
}