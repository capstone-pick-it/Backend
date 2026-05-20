package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.ProjectTeamMember;
import com.capstone.pickIt.domain.project.entity.RecruitmentConfirmStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectTeamMemberRepository extends JpaRepository<ProjectTeamMember, Long> {

    List<ProjectTeamMember> findByProjectTeam_Id(Long projectTeamId);

    @Query("""
    SELECT COUNT(ptm) > 0
    FROM ProjectTeamMember ptm
    WHERE ptm.projectTeam.id = :projectTeamId
      AND ptm.user.id = :userId
      AND ptm.leftAt IS NULL
      AND ptm.recruitmentConfirmStatus = :status
""")
    boolean existsActiveConfirmedMember(
            @Param("projectTeamId") Long projectTeamId,
            @Param("userId") Long userId,
            @Param("status") RecruitmentConfirmStatus status
    );
}