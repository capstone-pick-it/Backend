package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.CompletionDecision;
import com.capstone.pickIt.domain.project.entity.ProjectTeamCompletionApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectTeamCompletionApprovalRepository
        extends JpaRepository<ProjectTeamCompletionApproval, Long> {

    List<ProjectTeamCompletionApproval> findAllByCompletionRequestId(Long completionRequestId);

    Optional<ProjectTeamCompletionApproval> findByCompletionRequestIdAndUserId(
            Long completionRequestId,
            Long userId
    );

    long countByCompletionRequestIdAndDecision(
            Long completionRequestId,
            CompletionDecision decision
    );
}
