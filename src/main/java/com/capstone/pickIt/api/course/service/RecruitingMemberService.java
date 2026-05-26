package com.capstone.pickIt.api.course.service;

import com.capstone.pickIt.api.course.dto.request.RecruitingMemberSearchRequestDTO;
import com.capstone.pickIt.api.course.dto.response.RecruitingMemberListResponseDTO;

public interface RecruitingMemberService {

    RecruitingMemberListResponseDTO getRecruitingMembers(
            Long courseId,
            Long currentUserId,
            RecruitingMemberSearchRequestDTO request
    );
}
