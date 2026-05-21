package com.capstone.pickIt.api.project.service;

import com.capstone.pickIt.api.point.dto.response.PointResponseDTO;
import com.capstone.pickIt.api.point.service.PointService;
import com.capstone.pickIt.api.project.dto.response.ProjectEligibilityResponseDTO;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.capstone.pickIt.api.point.service.PointServiceImpl.PROJECT_REQUIRED_POINT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectEligibilityServiceImpl implements ProjectEligibilityService {

    private final PointService pointService;

    @Override
    @Transactional
    public ProjectEligibilityResponseDTO getMyProjectEligibility() {
        Long currentUserId = SecurityUtil.requireUserId();

        PointResponseDTO point = pointService.calculatePoint(currentUserId);

        boolean eligible = point.balance() >= PROJECT_REQUIRED_POINT;
        int shortagePoint = Math.max(PROJECT_REQUIRED_POINT - point.balance(), 0);

        return new ProjectEligibilityResponseDTO(
                currentUserId,
                eligible,
                point.balance(),
                PROJECT_REQUIRED_POINT,
                shortagePoint,
                eligible ? null : "프로젝트 참여를 위해 최소 20포인트가 필요합니다."
        );
    }
}