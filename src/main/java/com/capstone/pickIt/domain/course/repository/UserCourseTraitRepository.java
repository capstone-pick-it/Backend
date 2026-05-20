package com.capstone.pickIt.domain.course.repository;

import com.capstone.pickIt.domain.course.entity.UserCourseTrait;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCourseTraitRepository extends JpaRepository<UserCourseTrait, Long> {

    List<UserCourseTrait> findByUserCourseProfileId(Long userCourseProfileId);

    void deleteByUserCourseProfileId(Long userCourseProfileId);
}