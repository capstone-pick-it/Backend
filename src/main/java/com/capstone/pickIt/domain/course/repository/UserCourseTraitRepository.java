package com.capstone.pickIt.domain.course.repository;

import com.capstone.pickIt.domain.course.entity.UserCourseTrait;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserCourseTraitRepository extends JpaRepository<UserCourseTrait, Long> {

    List<UserCourseTrait> findByUserCourseProfileId(Long userCourseProfileId);

    @Modifying
    @Query("DELETE FROM UserCourseTrait uct WHERE uct.userCourseProfile.id = :userCourseProfileId")
    void deleteByUserCourseProfileId(Long userCourseProfileId);
}
