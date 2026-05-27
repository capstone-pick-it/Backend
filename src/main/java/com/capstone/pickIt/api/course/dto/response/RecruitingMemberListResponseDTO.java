package com.capstone.pickIt.api.course.dto.response;

import java.util.List;

public record RecruitingMemberListResponseDTO(
        Long courseId,
        String courseName,
        RecruitingMemberFilterResponseDTO filters,
        List<RecruitingMemberItemResponseDTO> content,
        int page,
        int size,
        long totalElements,
        boolean hasNext
) {
}
