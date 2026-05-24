package com.capstone.pickIt.api.user.dto.response;

import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import com.capstone.pickIt.domain.course.entity.UserCourseTrait;
import com.capstone.pickIt.domain.trait.entity.TraitSide;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CourseCardResponseDTO {

    private Long courseId;
    private String courseName;
    private String semester;
    private String importance;
    private List<TraitInfo> traits;

    @Getter
    @Builder
    public static class TraitInfo {
        private Long traitItemId;
        private String selectedType;
        private String selectedName;

        public static TraitInfo from(UserCourseTrait userCourseTrait) {
            TraitSide side = userCourseTrait.getSelectedSide();
            String selectedName = side == TraitSide.A
                    ? userCourseTrait.getTraitItem().getNameA()
                    : userCourseTrait.getTraitItem().getNameB();
            return TraitInfo.builder()
                    .traitItemId(userCourseTrait.getTraitItem().getTraitItemsId())
                    .selectedType(side.name())
                    .selectedName(selectedName)
                    .build();
        }
    }

    public static CourseCardResponseDTO from(UserCourseProfile profile, List<UserCourseTrait> traits) {
        return CourseCardResponseDTO.builder()
                .courseId(profile.getCourse().getId())
                .courseName(profile.getCourse().getCourseName())
                .semester(profile.getCourse().getSemester())
                .importance(profile.getImportanceLevel() != null ? profile.getImportanceLevel().name() : null)
                .traits(traits.stream().map(TraitInfo::from).toList())
                .build();
    }
}