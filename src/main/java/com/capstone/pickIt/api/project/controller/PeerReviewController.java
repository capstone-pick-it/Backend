package com.capstone.pickIt.api.project.controller;

import com.capstone.pickIt.api.project.dto.request.PeerReviewCreateRequestDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewStatusResponseDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewTargetListResponseDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewResponseDTO;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectTeamId}/peer-reviews")
public class PeerReviewController {

    @GetMapping("/targets")
    public ApiResponse<PeerReviewTargetListResponseDTO> getPeerReviewTargets(
            @PathVariable Long projectTeamId
    ) {
        PeerReviewTargetListResponseDTO response = null; // TODO: Service 연동
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PeerReviewResponseDTO> createPeerReview(
            @PathVariable Long projectTeamId,
            @Valid @RequestBody PeerReviewCreateRequestDTO request
    ) {
        PeerReviewResponseDTO response = null; // TODO: Service 연동
        return ApiResponse.onSuccess(SuccessCode.CREATED, response);
    }

    @GetMapping("/status")
    public ApiResponse<PeerReviewStatusResponseDTO> getPeerReviewStatus(
            @PathVariable Long projectTeamId
    ) {
        PeerReviewStatusResponseDTO response = null; // TODO: Service 연동
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }
}