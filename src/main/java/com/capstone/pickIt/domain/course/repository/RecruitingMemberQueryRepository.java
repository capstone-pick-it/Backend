package com.capstone.pickIt.domain.course.repository;

import com.capstone.pickIt.api.course.dto.request.RecruitingMemberSort;
import com.capstone.pickIt.api.course.dto.response.RecruitingMemberItemResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RecruitingMemberQueryRepository {

    Page<RecruitingMemberItemResponseDTO> searchRecruitingMembers(
            Long courseId,
            Long currentUserId,
            String keyword,
            RecruitingMemberSort sort,
            List<String> traits,
            boolean includeCompleted,
            int page,
            int size
    );
}
