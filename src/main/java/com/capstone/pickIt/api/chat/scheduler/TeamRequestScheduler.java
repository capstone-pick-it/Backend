package com.capstone.pickIt.api.chat.scheduler;

import com.capstone.pickIt.domain.project.repository.TeamRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class TeamRequestScheduler {

    private final TeamRequestRepository teamRequestRepository;
    private static final long ONE_HOUR = 60 * 60 * 1000L;

    @Scheduled(fixedDelay = ONE_HOUR)
    @Transactional
    public void rejectExpiredPendingTeamRequests() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime expiredBefore = now.minusHours(24);

        teamRequestRepository.rejectExpiredPendingRequests(expiredBefore, now);

        // TODO: WebSocket 연결 후 만료 처리된 팀원 요청에 대해 TEAM_REQUEST_REJECTED 이벤트 브로드캐스트 구현
    }
}
