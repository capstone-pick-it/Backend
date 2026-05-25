package com.capstone.pickIt.api.course.controller;

import com.capstone.pickIt.api.course.dto.request.RecruitingMemberSearchRequestDTO;
import com.capstone.pickIt.api.course.dto.response.RecruitingMemberListResponseDTO;
import com.capstone.pickIt.api.course.service.RecruitingMemberService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Course", description = "과목 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class RecruitingMemberController {

    private final RecruitingMemberService recruitingMemberService;

    @Operation(
            summary = "과목별 모집 카드 목록 조회",
            description = """
                    해당 과목의 수강생 모집 카드 목록을 조회합니다.
                    
                    - 로그인한 사용자의 카드는 기본적으로 제외됩니다.
                    - includeCompleted=false인 경우 RECRUITING 상태만 조회합니다.
                    - includeCompleted=true인 경우 RECRUITING, RECRUITMENT_COMPLETED 상태를 조회합니다.
                    - traits는 콤마(,)로 구분된 성향 이름 문자열입니다.
                    - traits가 여러 개 전달되면 선택한 성향을 모두 포함하는 사용자만 조회합니다.
                    - sort 기본값은 TRAIT_SIMILARITY_DESC입니다.
                    - page 기본값은 0, size 기본값은 20입니다.
                    
                    정렬 옵션:
                    - TRAIT_SIMILARITY_DESC: 성향 유사도 높은 순
                    - IMPORTANCE_DESC: 중요도 높은 순
                    - TEAM_LEVEL_DESC: 팀플 레벨 높은 순
                    - LATEST: 최신순
                    """
    )
    @GetMapping("/{courseId}/recruiting-members")
    public ApiResponse<RecruitingMemberListResponseDTO> getRecruitingMembers(
            @Parameter(description = "과목 ID", example = "1")
            @PathVariable Long courseId,

            @ModelAttribute RecruitingMemberSearchRequestDTO request
    ) {
        Long currentUserId = SecurityUtil.requireUserId();

        RecruitingMemberListResponseDTO response =
                recruitingMemberService.getRecruitingMembers(courseId, currentUserId, request);

        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }
}
