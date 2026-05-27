package com.capstone.pickIt.api.course.dto.response;

import java.util.List;

public record RecruitingMemberFilterResponseDTO(
        String keyword,
        String sort,
        List<String> traits,
        Boolean includeCompleted
) {
}
