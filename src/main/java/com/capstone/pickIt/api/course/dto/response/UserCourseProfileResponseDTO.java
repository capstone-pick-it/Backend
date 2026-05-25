package com.capstone.pickIt.api.course.dto.response;

import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import com.capstone.pickIt.domain.course.entity.UserCourseTrait;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserCourseProfileResponseDTO {

    private Long userCourseProfileId;
    private Long courseId;
    private String courseName;
    private String semester;
    private String importanceLevel;
    private String recruitmentStatus;
    private List<TraitResponseDTO> traits;

    @Getter
    @Builder
    public static class TraitResponseDTO {
        private Long traitItemsId;
        private String nameA;
        private String nameB;
        private String selectedSide;

        public static TraitResponseDTO from(UserCourseTrait trait) {
            return TraitResponseDTO.builder()
                    .traitItemsId(trait.getTraitItem().getTraitItemsId())
                    .nameA(trait.getTraitItem().getNameA())
                    .nameB(trait.getTraitItem().getNameB())
                    .selectedSide(trait.getSelectedSide().name())
                    .build();
        }
    }

    public static UserCourseProfileResponseDTO from(UserCourseProfile profile) {
        return UserCourseProfileResponseDTO.builder()
                .userCourseProfileId(profile.getId())
                .courseId(profile.getCourse().getId())
                .courseName(profile.getCourse().getCourseName())
                .semester(profile.getCourse().getSemester())
                .importanceLevel(profile.getImportanceLevel().name())
                .recruitmentStatus(profile.getRecruitmentStatus().name())
                .traits(profile.getTraits().stream()
                        .map(TraitResponseDTO::from)
                        .toList())
                .build();
    }
}
