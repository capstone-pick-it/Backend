package com.capstone.pickIt.api.user.controller;

import com.capstone.pickIt.api.user.dto.request.AddCourseRequestDTO;
import com.capstone.pickIt.api.user.dto.response.CourseCardResponseDTO;
import com.capstone.pickIt.api.user.service.MypageCourseService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mypage", description = "마이페이지 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MypageController {

    private final MypageCourseService mypageCourseService;

    @Operation(summary = "과목 카드 전체 조회", description = "마이페이지에서 본인의 모든 과목 팀플 카드를 조회합니다.")
    @GetMapping("/me/courses/card")
    public ApiResponse<List<CourseCardResponseDTO>> getCourseCards() {
        Long userId = SecurityUtil.requireUserId();
        return ApiResponse.onSuccess(SuccessCode.OK, mypageCourseService.getCourseCards(userId));
    }

    @Operation(summary = "강의 수정", description = "마이페이지에서 강의 정보 및 성향을 수정합니다.")
    @PostMapping("/me/courses/{courseId}")
    public ApiResponse<String> updateCourse(
            @PathVariable Long courseId,
            @RequestBody @Valid AddCourseRequestDTO request) {
        Long userId = SecurityUtil.requireUserId();
        mypageCourseService.updateCourse(userId, courseId, request);
        return ApiResponse.onSuccess(SuccessCode.OK, "강의 수정이 완료되었습니다.");
    }

    @Operation(summary = "강의 삭제", description = "마이페이지에서 강의를 삭제합니다.")
    @DeleteMapping("/me/courses/{courseId}")
    public ApiResponse<String> deleteCourse(@PathVariable Long courseId) {
        Long userId = SecurityUtil.requireUserId();
        mypageCourseService.deleteCourse(userId, courseId);
        return ApiResponse.onSuccess(SuccessCode.OK, "강의 삭제가 완료되었습니다.");
    }

    @Operation(summary = "강의 추가", description = "마이페이지에서 강의를 추가합니다.")
    @PostMapping("/me/courses")
    public ApiResponse<String> addCourse(@RequestBody @Valid AddCourseRequestDTO request) {
        Long userId = SecurityUtil.requireUserId();
        mypageCourseService.addCourse(userId, request);
        return ApiResponse.onSuccess(SuccessCode.OK, "강의 추가가 완료되었습니다.");
    }
}