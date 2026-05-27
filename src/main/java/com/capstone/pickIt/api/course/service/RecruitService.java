package com.capstone.pickIt.api.course.service;

import com.capstone.pickIt.api.course.dto.response.RecruitProfileResponseDTO;

import java.util.List;

public interface RecruitService {

    List<RecruitProfileResponseDTO> getRecruitProfiles(Long userId, Long courseId, boolean includeCompleted);
}
