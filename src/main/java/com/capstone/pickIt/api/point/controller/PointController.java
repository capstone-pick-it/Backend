package com.capstone.pickIt.api.point.controller;

import com.capstone.pickIt.api.point.dto.response.PointResponseDTO;
import com.capstone.pickIt.api.point.service.PointService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Point", description = "포인트 API")
@RestController
@RequestMapping("/api/me/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @Operation(summary = "현재 포인트 조회", description = "로그인한 사용자의 현재 포인트를 조회합니다.")
    @GetMapping
    public ApiResponse<PointResponseDTO> getMyPoint() {
        PointResponseDTO response = pointService.getMyPoint();
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }
}