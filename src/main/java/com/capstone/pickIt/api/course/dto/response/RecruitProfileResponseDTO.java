package com.capstone.pickIt.api.course.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecruitProfileResponseDTO {

    private Long userCourseProfileId;
    private Long userId;
    private String nickname;
    private String school;
    private String major;
    private Integer grade;
    private Long courseId;
    private String courseName;
    private String semester;
    private String importanceLevel;
    private String recruitmentStatus;
    private int traitScore;
    private List<TraitDTO> traits;

    @Getter
    @Builder
    public static class TraitDTO {
        private Long traitItemsId;
        private String nameA;
        private String nameB;
        private String selectedSide;
    }
}
