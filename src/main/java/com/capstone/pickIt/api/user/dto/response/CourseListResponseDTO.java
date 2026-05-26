package com.capstone.pickIt.api.user.dto.response;

import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import com.capstone.pickIt.domain.project.entity.ProjectTeamStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseListResponseDTO {

    private Long courseId;
    private String courseName;
    private ProjectTeamStatus projectStatus;

    public static CourseListResponseDTO from(UserCourseProfile profile, ProjectTeamStatus projectStatus) {
        return CourseListResponseDTO.builder()
                .courseId(profile.getCourse().getId())
                .courseName(profile.getCourse().getCourseName())
                .projectStatus(projectStatus)
                .build();
    }
}