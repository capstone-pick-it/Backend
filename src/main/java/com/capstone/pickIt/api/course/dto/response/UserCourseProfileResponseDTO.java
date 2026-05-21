package com.capstone.pickIt.api.course.dto.response;

import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCourseProfileResponseDTO {

    private Long userCourseProfileId;
    private Long courseId;
    private String courseName;
    private String semester;
    private String importanceLevel;
    private String recruitmentStatus;

    public static UserCourseProfileResponseDTO from(UserCourseProfile profile) {
        return UserCourseProfileResponseDTO.builder()
                .userCourseProfileId(profile.getId())
                .courseId(profile.getCourse().getId())
                .courseName(profile.getCourse().getCourseName())
                .semester(profile.getCourse().getSemester())
                .importanceLevel(profile.getImportanceLevel().name())
                .recruitmentStatus(profile.getRecruitmentStatus().name())
                .build();
    }
}
