package com.capstone.pickIt.domain.course.repository;

import com.capstone.pickIt.domain.course.entity.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    void deleteByUserIdAndCourseId(Long userId, Long courseId);

    @Query("""
            SELECT COUNT(uc)
            FROM UserCourse uc
            WHERE uc.course.id = :courseId
              AND uc.user.id IN (:userId1, :userId2)
            """)
    long countUsersByCourse(
            Long userId1,
            Long userId2,
            Long courseId
    );

    default boolean existsCommonCourse(
            Long userId1,
            Long userId2,
            Long courseId
    ) {
        return countUsersByCourse(userId1, userId2, courseId) == 2;
    }
}