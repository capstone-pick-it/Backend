package com.capstone.pickIt.api.chat.dto.response;

import java.util.List;

public class CommonCourseResponseDTO {

    public record CommonCourseList(
            List<CourseInfo> courses
    ) {
    }

    public record CourseInfo(
            Long courseId,
            String courseName
    ) {
    }
}
