package com.capstone.pickIt.api.chat.scheduler;

import com.capstone.pickIt.api.chat.converter.ChatRoomEventConverter;
import com.capstone.pickIt.api.chat.dto.response.ChatRoomEventResponseDTO;
import com.capstone.pickIt.domain.project.entity.TeamRequest;
import com.capstone.pickIt.domain.project.entity.TeamRequestStatus;
import com.capstone.pickIt.domain.project.repository.TeamRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamRequestScheduler {

    private final TeamRequestRepository teamRequestRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final long ONE_HOUR = 60 * 60 * 1000L;

    @Scheduled(fixedDelay = ONE_HOUR)
    @Transactional
    public void rejectExpiredPendingTeamRequests() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime expiredBefore = now.minusHours(24);

        List<TeamRequest> expiredTeamRequests =
                teamRequestRepository.findAllByTeamRequestStatusAndCreatedAtLessThanEqual(
                        TeamRequestStatus.PENDING,
                        expiredBefore
                );

        expiredTeamRequests.forEach(teamRequest -> {
            teamRequest.expire();

            ChatRoomEventResponseDTO.ChatRoomEvent event =
                    ChatRoomEventConverter.toTeamRequestRejectedEvent(teamRequest);

            messagingTemplate.convertAndSend(
                    "/topic/chatrooms/" + teamRequest.getChatRoom().getId(),
                    event
            );
        });
    }
}