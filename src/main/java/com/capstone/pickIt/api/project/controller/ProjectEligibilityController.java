package com.capstone.pickIt.api.project.controller;

import com.capstone.pickIt.api.project.dto.response.ProjectEligibilityResponseDTO;
import com.capstone.pickIt.api.project.service.ProjectEligibilityService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Project Eligibility", description = "프로젝트 참여 제한 API")
@RestController
@RequestMapping("/api/me/project-eligibility")
@RequiredArgsConstructor
public class ProjectEligibilityController {

    private final ProjectEligibilityService projectEligibilityService;

    @Operation(summary = "프로젝트 참여 가능 여부 조회", description = "사용자의 현재 포인트를 기준으로 프로젝트 참여 가능 여부를 조회합니다.")
    @GetMapping
    public ApiResponse<ProjectEligibilityResponseDTO> getMyProjectEligibility() {
        ProjectEligibilityResponseDTO response = projectEligibilityService.getMyProjectEligibility();
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }
}