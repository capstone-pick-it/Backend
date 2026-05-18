package com.capstone.pickIt.api.chat.converter;

import com.capstone.pickIt.api.chat.dto.response.CommonCourseResponseDTO;
import com.capstone.pickIt.domain.course.entity.Course;

import java.util.List;

public class CommonCourseConverter {

    private CommonCourseConverter() {
    }

    public static CommonCourseResponseDTO.CommonCourseList toCommonCourseList(
            List<Course> courses
    ) {
        return new CommonCourseResponseDTO.CommonCourseList(
                courses.stream()
                        .map(course -> new CommonCourseResponseDTO.CourseInfo(
                                course.getId(),
                                course.getCourseName()
                        ))
                        .toList()
        );
    }
}
