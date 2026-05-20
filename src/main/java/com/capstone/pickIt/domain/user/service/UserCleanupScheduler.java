package com.capstone.pickIt.domain.user.service;

import com.capstone.pickIt.domain.point.repository.PointRepository;
import com.capstone.pickIt.domain.point.repository.PointTransactionRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserRepository userRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final PointRepository pointRepository;

    // 매일 새벽 3시에 탈퇴 후 30일 지난 계정 하드 딜리트
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    @Transactional
    public void deleteWithdrawnUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        List<User> targets = userRepository.findByDeletedAtBeforeAndDeletedAtIsNotNull(threshold);

        List<Long> userIds = targets.stream().map(User::getId).toList();
        pointTransactionRepository.deleteAllByUserIdIn(userIds);
        pointRepository.deleteAllByUserIdIn(userIds);

        userRepository.deleteAll(targets);
    }
}