package com.capstone.pickIt.api.chat.dto.response;

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

    public record CourseInfo(
            Long courseId,
            String courseName
    ) {
    }
}
