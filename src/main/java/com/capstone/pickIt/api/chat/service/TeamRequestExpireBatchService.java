package com.capstone.pickIt.api.chat.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TeamRequestExpireBatchService {

    private static final int BATCH_SIZE = 200;

    private final TeamRequestRepository teamRequestRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public boolean expireBatch(LocalDateTime expiredBefore) {
        Page<TeamRequest> expiredTeamRequests =
                teamRequestRepository.findByTeamRequestStatusAndCreatedAtLessThanEqual(
                        TeamRequestStatus.PENDING,
                        expiredBefore,
                        PageRequest.of(0, BATCH_SIZE)
                );

        if (expiredTeamRequests.isEmpty()) {
            return false;
        }

        expiredTeamRequests.forEach(teamRequest -> {
            teamRequest.expire();

            ChatRoomEventResponseDTO.ChatRoomEvent event =
                    ChatRoomEventConverter.toTeamRequestRejectedEvent(teamRequest);

            eventPublisher.publishEvent(
                    new ChatRoomBroadcastEvent(teamRequest.getChatRoom().getId(), event)
            );
        });

        return expiredTeamRequests.hasNext();
    }
}
