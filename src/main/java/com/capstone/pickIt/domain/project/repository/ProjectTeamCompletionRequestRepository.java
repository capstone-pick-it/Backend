package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.CompletionRequestStatus;
import com.capstone.pickIt.domain.project.entity.ProjectTeamCompletionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectTeamCompletionRequestRepository
        extends JpaRepository<ProjectTeamCompletionRequest, Long> {

    Optional<ProjectTeamCompletionRequest> findTopByProjectTeamIdAndStatusOrderByRequestedAtDesc(
            Long projectTeamId,
            CompletionRequestStatus status
    );

    boolean existsByProjectTeamIdAndStatus(
            Long projectTeamId,
            CompletionRequestStatus status
    );
}