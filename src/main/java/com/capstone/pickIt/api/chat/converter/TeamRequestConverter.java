package com.capstone.pickIt.api.chat.converter;

import com.capstone.pickIt.api.chat.dto.response.TeamRequestResponseDTO;
import com.capstone.pickIt.domain.project.entity.TeamRequest;

public class TeamRequestConverter {

    private TeamRequestConverter() {
    }

    public static TeamRequestResponseDTO.Create toCreateResponse(
            TeamRequest teamRequest,
            Long currentUserId
    ) {
        String role = teamRequest.getSender().getId().equals(currentUserId)
                ? "SENDER"
                : "RECEIVER";

        return new TeamRequestResponseDTO.Create(
                teamRequest.getId(),
                teamRequest.getChatRoom().getId(),
                new TeamRequestResponseDTO.CourseInfo(
                        teamRequest.getCourse().getId(),
                        teamRequest.getCourse().getCourseName()
                ),
                teamRequest.getSender().getId(),
                teamRequest.getReceiver().getId(),
                teamRequest.getTeamRequestStatus().name(),
                role,
                teamRequest.getCreatedAt()
        );
    }
}
