package com.capstone.pickIt.domain.course.repository;

import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCourseProfileRepository extends JpaRepository<UserCourseProfile, Long> {

    List<UserCourseProfile> findByUserIdAndDeletedAtIsNull(Long userId);

    Optional<UserCourseProfile> findByUserIdAndCourseId(Long userId, Long courseId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}