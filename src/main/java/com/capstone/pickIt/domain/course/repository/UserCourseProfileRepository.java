package com.capstone.pickIt.domain.course.repository;

import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
          AND ucp1.recruitmentStatus = 'RECRUITING'
          AND ucp2.recruitmentStatus = 'RECRUITING'
          AND NOT EXISTS (
              SELECT 1
              FROM TeamRequest tr
              WHERE tr.sender.id = :currentUserId
                AND tr.course.id = ucp1.course.id
                AND tr.teamRequestStatus = 'PENDING'
          )
        ORDER BY ucp1.course.courseName ASC
        """)
    List<Course> findRequestableCommonCourses(
            Long currentUserId,
            Long opponentUserId
    );

    Optional<UserCourseProfile> findByUserIdAndCourseIdAndDeletedAtIsNull(
            Long userId,
            Long courseId
    );

    List<UserCourseProfile> findAllByUserIdAndDeletedAtIsNull(Long userId);

    List<UserCourseProfile> findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId);
}