package com.capstone.pickIt.domain.course.repository;

import com.capstone.pickIt.domain.course.entity.Course;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseNameAndSemester(String courseName, String semester);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.id = :courseId")
    Optional<Course> findByIdForUpdate(@Param("courseId") Long courseId);
}