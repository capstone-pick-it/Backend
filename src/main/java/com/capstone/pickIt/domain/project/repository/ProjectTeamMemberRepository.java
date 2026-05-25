package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.ProjectTeamMember;
import com.capstone.pickIt.domain.project.entity.ProjectTeamStatus;
import com.capstone.pickIt.domain.project.entity.RecruitmentConfirmStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectTeamMemberRepository extends JpaRepository<ProjectTeamMember, Long> {

    List<ProjectTeamMember> findByProjectTeam_Id(Long projectTeamId);

    Optional<ProjectTeamMember> findByProjectTeamIdAndUserIdAndLeftAtIsNull(Long projectTeamId, Long userId);

    List<ProjectTeamMember> findAllByProjectTeamIdAndLeftAtIsNull(Long projectTeamId);

    boolean existsByProjectTeamIdAndUserId(Long projectTeamId, Long userId);

    @Query("""
        SELECT COUNT(ptm) > 0
        FROM ProjectTeamMember ptm
        JOIN ptm.projectTeam pt
        WHERE pt.course.id = :courseId
        AND ptm.user.id = :userId
        AND ptm.leftAt IS NULL
        AND pt.status IN :statuses
    """)
    boolean existsActiveTeamByCourseAndUser(
            @Param("courseId") Long courseId,
            @Param("userId") Long userId,
            @Param("statuses") List<ProjectTeamStatus> statuses
    );

    List<ProjectTeamMember> findAllByUserIdAndRecruitmentConfirmStatusAndLeftAtIsNullOrderByJoinedAtDesc(
            Long userId,
            RecruitmentConfirmStatus recruitmentConfirmStatus
    );

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

    @Query("""
            SELECT COUNT(ptm)
            FROM ProjectTeamMember ptm
            WHERE ptm.user.id = :userId
              AND ptm.recruitmentConfirmStatus = 'CONFIRMED'
              AND ptm.leftAt IS NULL
            """)
    long countConfirmedByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT COUNT(ptm)
            FROM ProjectTeamMember ptm
            WHERE ptm.user.id = :userId
              AND ptm.recruitmentConfirmStatus = 'CONFIRMED'
              AND ptm.leftAt IS NULL
              AND ptm.projectTeam.status = 'DONE'
            """)
    long countDoneConfirmedByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT ptm
        FROM ProjectTeamMember ptm
        JOIN FETCH ptm.user
        WHERE ptm.projectTeam.id = :projectTeamId
          AND ptm.leftAt IS NULL
          AND ptm.recruitmentConfirmStatus = :status
        ORDER BY
          CASE WHEN ptm.role = com.capstone.pickIt.domain.project.entity.ProjectTeamMemberRole.LEAD THEN 0 ELSE 1 END,
          ptm.joinedAt ASC
    """)
    List<ProjectTeamMember> findActiveConfirmedMembersWithUser(
            @Param("projectTeamId") Long projectTeamId,
            @Param("status") RecruitmentConfirmStatus status
    );

    @Query("""
        SELECT ptm
        FROM ProjectTeamMember ptm
        JOIN FETCH ptm.projectTeam pt
        JOIN FETCH pt.course
        WHERE ptm.user.id = :userId
          AND ptm.recruitmentConfirmStatus = 'CONFIRMED'
          AND ptm.leftAt IS NULL
          AND (:status IS NULL OR pt.status = :status)
        ORDER BY ptm.joinedAt DESC
    """)
    List<ProjectTeamMember> findActiveConfirmedMembershipsWithTeamAndCourse(
            @Param("userId") Long userId,
            @Param("status") ProjectTeamStatus status
    );

    @Query("""
        SELECT ptm
        FROM ProjectTeamMember ptm
        JOIN FETCH ptm.user
        WHERE ptm.projectTeam.id IN :projectTeamIds
          AND ptm.leftAt IS NULL
          AND ptm.recruitmentConfirmStatus = 'CONFIRMED'
        ORDER BY ptm.projectTeam.id, ptm.joinedAt ASC
    """)
    List<ProjectTeamMember> findActiveConfirmedMembersWithUserByProjectTeamIds(
            @Param("projectTeamIds") List<Long> projectTeamIds
    );
}