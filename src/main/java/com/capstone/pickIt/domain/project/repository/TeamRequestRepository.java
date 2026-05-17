package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.TeamRequest;
import com.capstone.pickIt.domain.project.entity.TeamRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRequestRepository extends JpaRepository<TeamRequest, Long> {

    boolean existsBySenderIdAndCourseIdAndTeamRequestStatus(
            Long senderId,
            Long courseId,
            TeamRequestStatus teamRequestStatus
    );

    boolean existsBySenderIdAndReceiverIdAndTeamRequestStatus(
            Long senderId,
            Long receiverId,
            TeamRequestStatus teamRequestStatus
    );

    boolean existsByChatRoomIdAndReceiverIdAndTeamRequestStatus(
            Long chatRoomId,
            Long receiverId,
            TeamRequestStatus teamRequestStatus
    );
}
