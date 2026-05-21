package com.capstone.pickIt.api.course.controller;

import com.capstone.pickIt.api.course.dto.request.ProfileCreateRequestDTO;
import com.capstone.pickIt.api.course.dto.request.ProfileStatusUpdateRequestDTO;
import com.capstone.pickIt.api.course.dto.request.ProfileUpdateRequestDTO;
import com.capstone.pickIt.api.course.dto.response.UserCourseProfileResponseDTO;
import com.capstone.pickIt.api.course.service.UserCourseProfileService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Course Profile", description = "모집 프로필(카드) API")
@RestController
@RequestMapping("/api/courses/profiles")
@RequiredArgsConstructor
public class UserCourseProfileController {

    private final UserCourseProfileService userCourseProfileService;

    @Operation(summary = "모집 프로필 목록 조회", description = "로그인한 사용자의 모집 프로필 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<UserCourseProfileResponseDTO>> getProfiles() {
        Long userId = SecurityUtil.requireUserId();
        return ApiResponse.onSuccess(SuccessCode.OK, userCourseProfileService.getProfiles(userId));
    }

    @Operation(summary = "모집 프로필 상세 조회", description = "특정 모집 프로필의 상세 정보를 조회합니다.")
    @GetMapping("/{userCourseProfileId}")
    public ApiResponse<UserCourseProfileResponseDTO> getProfile(
            @PathVariable Long userCourseProfileId) {
        Long userId = SecurityUtil.requireUserId();
        return ApiResponse.onSuccess(SuccessCode.OK, userCourseProfileService.getProfile(userId, userCourseProfileId));
    }

    @Operation(summary = "모집 프로필 생성", description = "강의에 대한 모집 프로필을 생성합니다.")
    @PostMapping
    public ApiResponse<UserCourseProfileResponseDTO> createProfile(
            @RequestBody @Valid ProfileCreateRequestDTO request) {
        Long userId = SecurityUtil.requireUserId();
        return ApiResponse.onSuccess(SuccessCode.CREATED, userCourseProfileService.createProfile(userId, request));
    }

    @Operation(summary = "모집 프로필 수정", description = "모집 프로필의 중요도를 수정합니다.")
    @PutMapping("/{userCourseProfileId}")
    public ApiResponse<UserCourseProfileResponseDTO> updateProfile(
            @PathVariable Long userCourseProfileId,
            @RequestBody @Valid ProfileUpdateRequestDTO request) {
        Long userId = SecurityUtil.requireUserId();
        return ApiResponse.onSuccess(SuccessCode.OK, userCourseProfileService.updateProfile(userId, userCourseProfileId, request));
    }

    @Operation(summary = "모집 프로필 삭제", description = "모집 프로필을 삭제합니다.")
    @DeleteMapping("/{userCourseProfileId}")
    public ApiResponse<Void> deleteProfile(
            @PathVariable Long userCourseProfileId) {
        Long userId = SecurityUtil.requireUserId();
        userCourseProfileService.deleteProfile(userId, userCourseProfileId);
        return ApiResponse.onSuccess(SuccessCode.OK, null);
    }

    @Operation(summary = "모집 프로필 상태 변경", description = "모집 프로필의 모집 상태를 변경합니다.")
    @PatchMapping("/{userCourseProfileId}/status")
    public ApiResponse<UserCourseProfileResponseDTO> updateStatus(
            @PathVariable Long userCourseProfileId,
            @RequestBody @Valid ProfileStatusUpdateRequestDTO request) {
        Long userId = SecurityUtil.requireUserId();
        return ApiResponse.onSuccess(SuccessCode.OK, userCourseProfileService.updateStatus(userId, userCourseProfileId, request));
    }
}
