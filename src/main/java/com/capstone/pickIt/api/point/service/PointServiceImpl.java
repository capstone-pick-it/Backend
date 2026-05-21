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
import java.time.temporal.ChronoUnit;

import static com.capstone.pickIt.domain.point.policy.PointPolicy.PROJECT_REQUIRED_POINT;
import static com.capstone.pickIt.domain.point.policy.PointPolicy.RECOVERY_INTERVAL_DAYS;
import static com.capstone.pickIt.domain.point.policy.PointPolicy.WEEKLY_RECOVERY_POINT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PointResponseDTO getMyPoint() {
        Long currentUserId = SecurityUtil.requireUserId();
        return refreshAndGetPoint(currentUserId);
    }

    @Override
    @Transactional
    public PointResponseDTO refreshAndGetPoint(Long userId) {
        Point point = findPointForUpdate(userId);
        LocalDateTime lastRecoveredAt = findLastRecoveredAt(userId);

        RecoveryResult recovery = calculateRecovery(point, lastRecoveredAt);
        boolean recoveryApplied = recovery.recoveredPoint() > 0;

        if (recoveryApplied) {
            point.add(recovery.recoveredPoint());

            saveRecoveryTransaction(
                    userId,
                    point,
                    recovery.recoveredPoint(),
                    recovery.elapsedWeeks()
            );

            lastRecoveredAt = LocalDateTime.now();
        }

        return new PointResponseDTO(
                userId,
                point.getBalance(),
                recoveryApplied,
                recovery.recoveredPoint(),
                lastRecoveredAt
        );
    }

    private RecoveryResult calculateRecovery(Point point, LocalDateTime lastRecoveredAt) {
        if (point.getBalance() >= PROJECT_REQUIRED_POINT) {
            return new RecoveryResult(0, 0);
        }

        long elapsedWeeks = calculateElapsedWeeks(lastRecoveredAt);

        if (elapsedWeeks <= 0) {
            return new RecoveryResult(0, elapsedWeeks);
        }

        int recoverablePoint = Math.toIntExact(elapsedWeeks * WEEKLY_RECOVERY_POINT);
        int shortagePoint = PROJECT_REQUIRED_POINT - point.getBalance();
        int recoveredPoint = Math.min(recoverablePoint, shortagePoint);

        return new RecoveryResult(recoveredPoint, elapsedWeeks);
    }

    private long calculateElapsedWeeks(LocalDateTime lastRecoveredAt) {
        if (lastRecoveredAt == null) {
            return 1;
        }

        long elapsedDays = ChronoUnit.DAYS.between(lastRecoveredAt, LocalDateTime.now());
        return elapsedDays / RECOVERY_INTERVAL_DAYS;
    }

    private void saveRecoveryTransaction(
            Long userId,
            Point point,
            int recoveredPoint,
            long elapsedWeeks
    ) {
        PointTransaction transaction = PointTransaction.builder()
                .user(findUser(userId))
                .transactionType(PointTransactionType.WEEKLY_RECOVERY)
                .amount(recoveredPoint)
                .balanceAfter(point.getBalance())
                .description("주간 포인트 자동 회복 (%d주 누적)".formatted(elapsedWeeks))
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

    private Point findPointForUpdate(Long userId) {
        return pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.USER_NOT_FOUND));
    }

    private record RecoveryResult(
            int recoveredPoint,
            long elapsedWeeks
    ) {
    }
}