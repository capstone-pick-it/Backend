package com.capstone.pickIt.api.chat.dto.response;

import com.capstone.pickIt.domain.project.entity.TeamRequestRole;
import com.capstone.pickIt.domain.project.entity.TeamRequestStatus;

import java.time.LocalDateTime;

public class TeamRequestResponseDTO {

    public record Create(
            Long teamRequestId,
            Long chatRoomId,
            CourseInfo course,
            Long senderId,
            Long receiverId,
            String status,
            String role,
            LocalDateTime createdAt
    ) {
    }

    public record Respond(
            Long teamRequestId,
            Long chatRoomId,
            String status,
            LocalDateTime respondedAt
    ) {
    }

    public record CourseInfo(
            Long courseId,
            String courseName
    ) {
    }

    public record LatestStatus(
            Long teamRequestId,
            Long chatRoomId,
            Long courseId,
            String courseName,
            TeamRequestStatus status,
            TeamRequestRole role,
            LocalDateTime createdAt,
            LocalDateTime respondedAt
    ) {
    }
}
