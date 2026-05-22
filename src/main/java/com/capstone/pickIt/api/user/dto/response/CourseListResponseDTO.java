package com.capstone.pickIt.api.user.dto.response;

import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseListResponseDTO {

    private Long courseId;
    private String courseName;

    public static CourseListResponseDTO from(UserCourseProfile profile) {
        return CourseListResponseDTO.builder()
                .courseId(profile.getCourse().getId())
                .courseName(profile.getCourse().getCourseName())
                .build();
    }
}