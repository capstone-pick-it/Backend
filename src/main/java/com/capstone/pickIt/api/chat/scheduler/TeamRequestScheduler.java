package com.capstone.pickIt.api.chat.scheduler;

import com.capstone.pickIt.api.chat.converter.ChatRoomEventConverter;
import com.capstone.pickIt.api.chat.dto.response.ChatRoomEventResponseDTO;
import com.capstone.pickIt.api.chat.event.ChatRoomBroadcastEvent;
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
    private static final int BATCH_SIZE = 200;

    private final TeamRequestRepository teamRequestRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedDelay = ONE_HOUR)
    @Transactional
    public void rejectExpiredPendingTeamRequests() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime expiredBefore = now.minusHours(24);


        while (true) {
            Page<TeamRequest> expiredTeamRequests =
                    teamRequestRepository.findByTeamRequestStatusAndCreatedAtLessThanEqual(
                            TeamRequestStatus.PENDING,
                            expiredBefore,
                            PageRequest.of(0, BATCH_SIZE)
                    );

            if (expiredTeamRequests.isEmpty()) {
                break;
            }

            expiredTeamRequests.forEach(teamRequest -> {
                teamRequest.expire();

                ChatRoomEventResponseDTO.ChatRoomEvent event =
                        ChatRoomEventConverter.toTeamRequestRejectedEvent(teamRequest);

                eventPublisher.publishEvent(
                        new ChatRoomBroadcastEvent(teamRequest.getChatRoom().getId(), event)
                );
            });
        }
    }
}