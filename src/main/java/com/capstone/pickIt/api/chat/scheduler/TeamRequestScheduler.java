package com.capstone.pickIt.api.chat.scheduler;

import com.capstone.pickIt.api.chat.converter.ChatRoomEventConverter;
import com.capstone.pickIt.api.chat.dto.response.ChatRoomEventResponseDTO;
import com.capstone.pickIt.api.chat.event.ChatRoomBroadcastEvent;
import com.capstone.pickIt.api.chat.service.TeamRequestExpireBatchService;
import com.capstone.pickIt.domain.project.entity.TeamRequest;
import com.capstone.pickIt.domain.project.entity.TeamRequestStatus;
import com.capstone.pickIt.domain.project.repository.TeamRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class TeamRequestScheduler {

    private static final long ONE_HOUR = 60 * 60 * 1000L;

    private final TeamRequestExpireBatchService teamRequestExpireBatchService;

    @Scheduled(fixedDelay = ONE_HOUR)
    @Transactional
    public void rejectExpiredPendingTeamRequests() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime expiredBefore = now.minusHours(24);

        while (teamRequestExpireBatchService.expireBatch(expiredBefore)) {
            // 만료 요청이 남아 있으면 다음 배치 계속 처리
        }
    }
}