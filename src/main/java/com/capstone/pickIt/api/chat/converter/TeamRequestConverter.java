package com.capstone.pickIt.api.chat.converter;

import com.capstone.pickIt.api.chat.dto.response.TeamRequestResponseDTO;
import com.capstone.pickIt.domain.project.entity.TeamRequest;
import com.capstone.pickIt.domain.project.entity.TeamRequestRole;

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

    public static TeamRequestResponseDTO.Respond toRespondResponse(TeamRequest teamRequest) {
        return new TeamRequestResponseDTO.Respond(
                teamRequest.getId(),
                teamRequest.getChatRoom().getId(),
                teamRequest.getTeamRequestStatus().name(),
                teamRequest.getRespondedAt()
        );
    }

    public static TeamRequestResponseDTO.LatestStatus toLatestStatusResponse(
            TeamRequest teamRequest,
            Long currentUserId
    ) {
        TeamRequestRole role = teamRequest.getSender().getId().equals(currentUserId)
                ? TeamRequestRole.SENDER
                : TeamRequestRole.RECEIVER;

        return new TeamRequestResponseDTO.LatestStatus(
                teamRequest.getId(),
                teamRequest.getChatRoom().getId(),
                teamRequest.getCourse().getId(),
                teamRequest.getCourse().getCourseName(),
                teamRequest.getTeamRequestStatus(),
                role,
                teamRequest.getCreatedAt(),
                teamRequest.getRespondedAt()
        );
    }
}
