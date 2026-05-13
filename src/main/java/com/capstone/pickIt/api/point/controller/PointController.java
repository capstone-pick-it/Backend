package com.capstone.pickIt.api.point.controller;

import com.capstone.pickIt.api.point.dto.response.PointResponseDTO;
import com.capstone.pickIt.api.point.dto.response.PointTransactionListResponseDTO;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Point", description = "포인트 API")
@RestController
@RequestMapping("/api/me/points")
public class PointController {

    @Operation(summary = "현재 포인트 조회", description = "로그인한 사용자의 현재 포인트를 조회합니다.")
    @GetMapping
    public ApiResponse<PointResponseDTO> getMyPoint() {
        PointResponseDTO response = null; // TODO: Service 연동
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @Operation(summary = "포인트 이력 조회", description = "로그인한 사용자의 포인트 변동 이력을 페이지네이션으로 조회합니다.")
    @GetMapping("/transactions")
    public ApiResponse<PointTransactionListResponseDTO> getMyPointTransactions(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        PointTransactionListResponseDTO response = null; // TODO: Service 연동
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }
}