package com.capstone.pickIt.api.user.controller;

import com.capstone.pickIt.api.user.dto.request.UserDefaultTraitRequestDTO;
import com.capstone.pickIt.api.user.dto.response.UserDefaultTraitResponseDTO;
import com.capstone.pickIt.api.user.service.UserDefaultTraitService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "User Default Trait", description = "기본 팀플 성향 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserDefaultTraitController {

    private final UserDefaultTraitService userDefaultTraitService;

    @Operation(summary = "기본 팀플 성향 조회", description = "로그인한 사용자의 기본 팀플 성향을 조회합니다.")
    @GetMapping("/me/traits/default")
    public ApiResponse<List<UserDefaultTraitResponseDTO>> getDefaultTraits() {
        Long userId = SecurityUtil.requireUserId();
        return ApiResponse.onSuccess(SuccessCode.OK, userDefaultTraitService.getDefaultTraits(userId));
    }

    @Operation(summary = "기본 팀플 성향 등록/수정", description = "로그인한 사용자의 기본 팀플 성향을 등록하거나 수정합니다.")
    @PutMapping("/me/traits/default")
    public ApiResponse<List<UserDefaultTraitResponseDTO>> updateDefaultTraits(
            @RequestBody @Valid List<UserDefaultTraitRequestDTO> requests) {
        Long userId = SecurityUtil.requireUserId();
        return ApiResponse.onSuccess(SuccessCode.OK, userDefaultTraitService.updateDefaultTraits(userId, requests));
    }
}