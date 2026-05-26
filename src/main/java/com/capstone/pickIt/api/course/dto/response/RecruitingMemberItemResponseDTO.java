package com.capstone.pickIt.api.course.dto.response;

import com.capstone.pickIt.domain.course.entity.ImportanceLevel;
import com.capstone.pickIt.domain.course.entity.RecruitmentStatus;

import java.math.BigDecimal;
import java.util.List;

public record RecruitingMemberItemResponseDTO(
        Long userCourseProfileId,
        Long userId,
        String name,
        String department,
        Integer grade,
        RecruitmentStatus recruitmentStatus,
        ImportanceLevel importance,
        List<String> traits,
        Integer teamLevel,
        Integer point,
        Long projectCount,
        BigDecimal completionRate,
        BigDecimal averagePeerRating,
        Boolean canChat
) {
}
