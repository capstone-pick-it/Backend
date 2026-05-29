package com.capstone.pickIt.domain.course.repository;

import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.domain.course.entity.RecruitmentStatus;
import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import com.capstone.pickIt.domain.project.entity.TeamRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCourseProfileRepository extends JpaRepository<UserCourseProfile, Long> {

    List<UserCourseProfile> findByUserIdAndDeletedAtIsNull(Long userId);

    Optional<UserCourseProfile> findByUserIdAndCourseId(Long userId, Long courseId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    boolean existsByUserIdAndCourseIdAndDeletedAtIsNull(Long userId, Long courseId);

    @Query("""
        SELECT ucp1.course
        FROM UserCourseProfile ucp1
        JOIN UserCourseProfile ucp2
          ON ucp2.course.id = ucp1.course.id
        WHERE ucp1.user.id = :currentUserId
          AND ucp2.user.id = :opponentUserId
          AND ucp1.deletedAt IS NULL
          AND ucp2.deletedAt IS NULL
          AND ucp1.recruitmentStatus = :recruitingStatus
          AND ucp2.recruitmentStatus = :recruitingStatus
          AND NOT EXISTS (
              SELECT 1
              FROM TeamRequest tr
              WHERE tr.sender.id = :currentUserId
                AND tr.course.id = ucp1.course.id
                AND tr.teamRequestStatus = :pendingStatus
          )
          AND NOT EXISTS (
              SELECT 1
              FROM TeamRequest tr
              WHERE tr.course.id = ucp1.course.id
                AND tr.teamRequestStatus = :acceptedStatus
                AND (
                  (tr.sender.id = :currentUserId AND tr.receiver.id = :opponentUserId)
                  OR
                  (tr.sender.id = :opponentUserId AND tr.receiver.id = :currentUserId)
                )
          )
        ORDER BY ucp1.course.courseName ASC
        """)
    List<Course> findRequestableCommonCourses(
            @Param("currentUserId") Long currentUserId,
            @Param("opponentUserId") Long opponentUserId,
            @Param("recruitingStatus") RecruitmentStatus recruitingStatus,
            @Param("pendingStatus") TeamRequestStatus pendingStatus,
            @Param("acceptedStatus") TeamRequestStatus acceptedStatus
    );

    Optional<UserCourseProfile> findByUserIdAndCourseIdAndDeletedAtIsNull(
            Long userId,
            Long courseId
    );

    List<UserCourseProfile> findAllByUserIdAndDeletedAtIsNull(Long userId);

    List<UserCourseProfile> findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId);

    @Query("""
        SELECT ucp FROM UserCourseProfile ucp
        WHERE ucp.course.id = :courseId
          AND ucp.user.id != :excludeUserId
          AND ucp.deletedAt IS NULL
          AND ucp.recruitmentStatus IN :statuses
        """)
    List<UserCourseProfile> findProfilesExcludingUserByStatuses(
            @Param("courseId") Long courseId,
            @Param("excludeUserId") Long excludeUserId,
            @Param("statuses") List<RecruitmentStatus> statuses
    );
}