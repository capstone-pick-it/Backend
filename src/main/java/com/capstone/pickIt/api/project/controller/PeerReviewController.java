package com.capstone.pickIt.api.project.controller;

import com.capstone.pickIt.api.project.dto.request.PeerReviewCreateRequestDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewResponseDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewStatusResponseDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewTargetListResponseDTO;
import com.capstone.pickIt.api.project.service.PeerReviewService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Peer Review", description = "팀플 종료 평가 API")
@RestController
@RequestMapping("/projects/{projectTeamId}/peer-reviews")
@RequiredArgsConstructor
public class PeerReviewController {

    private final PeerReviewService peerReviewService;

    @Operation(summary = "평가 대상 목록 조회", description = "로그인한 사용자가 평가해야 할 팀원 목록을 조회합니다.")
    @GetMapping("/targets")
    public ApiResponse<PeerReviewTargetListResponseDTO> getPeerReviewTargets(
            @PathVariable Long projectTeamId
    ) {
        PeerReviewTargetListResponseDTO response =
                peerReviewService.getPeerReviewTargets(projectTeamId);

        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @Operation(summary = "특정 팀원 평가 등록", description = "특정 팀원에 대한 동료 평가를 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PeerReviewResponseDTO> createPeerReview(
            @PathVariable Long projectTeamId,
            @Valid @RequestBody PeerReviewCreateRequestDTO request
    ) {
        PeerReviewResponseDTO response =
                peerReviewService.createPeerReview(projectTeamId, request);

        return ApiResponse.onSuccess(SuccessCode.CREATED, response);
    }

    @Operation(summary = "프로젝트별 평가 진행 상태 조회", description = "프로젝트 팀원별 평가 제출 진행 상태를 조회합니다.")
    @GetMapping("/status")
    public ApiResponse<PeerReviewStatusResponseDTO> getPeerReviewStatus(
            @PathVariable Long projectTeamId
    ) {
        PeerReviewStatusResponseDTO response =
                peerReviewService.getPeerReviewStatus(projectTeamId);

        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }
}