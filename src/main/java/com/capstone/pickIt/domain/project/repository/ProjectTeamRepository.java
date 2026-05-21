package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.ProjectTeam;
import com.capstone.pickIt.domain.project.entity.ProjectTeamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProjectTeamRepository extends JpaRepository<ProjectTeam, Long> {

    @Query("""
        SELECT pt
        FROM ProjectTeam pt
        JOIN ProjectTeamMember ptm ON ptm.projectTeam = pt
        WHERE pt.course.id = :courseId
          AND pt.status = 'RECRUITING'
          AND ptm.user.id IN (:senderId, :receiverId)
        ORDER BY pt.createdAt DESC
        """)
    Optional<ProjectTeam> findRecruitingTeamByCourseAndMember(
            Long courseId,
            Long senderId,
            Long receiverId
    );
}