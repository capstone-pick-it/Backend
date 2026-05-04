package com.capstone.pickIt.domain.course.repository;

import com.capstone.pickIt.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}