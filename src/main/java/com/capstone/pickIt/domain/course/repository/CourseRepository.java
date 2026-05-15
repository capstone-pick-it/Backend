package com.capstone.pickIt.domain.course.repository;

import com.capstone.pickIt.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseNameAndSemester(String courseName, String semester);
}