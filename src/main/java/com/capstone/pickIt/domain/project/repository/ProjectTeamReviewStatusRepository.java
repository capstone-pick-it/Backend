package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.ProjectTeamReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectTeamReviewStatusRepository
        extends JpaRepository<ProjectTeamReviewStatus, Long> {

    List<ProjectTeamReviewStatus> findAllByProjectTeamId(Long projectTeamId);

    Optional<ProjectTeamReviewStatus> findByProjectTeamIdAndUserId(
            Long projectTeamId,
            Long userId
    );
}