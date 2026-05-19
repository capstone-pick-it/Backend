package com.capstone.pickIt.api.user.controller;

import com.capstone.pickIt.api.user.dto.request.EmailSendRequestDTO;
import com.capstone.pickIt.api.user.dto.request.EmailVerifyRequestDTO;
import com.capstone.pickIt.api.user.dto.request.LoginRequestDTO;
import com.capstone.pickIt.api.user.dto.request.OnboardingBasicInfoRequestDTO;
import com.capstone.pickIt.api.user.dto.request.OnboardingPersonalityRequestDTO;
import com.capstone.pickIt.api.user.dto.request.PasswordResetRequestDTO;
import com.capstone.pickIt.api.user.dto.request.PasswordResetSendRequestDTO;
import com.capstone.pickIt.api.user.dto.request.PasswordResetVerifyRequestDTO;
import com.capstone.pickIt.api.user.dto.request.TokenRefreshRequestDTO;
import com.capstone.pickIt.api.user.dto.request.UserRequestDTO;
import com.capstone.pickIt.api.user.dto.response.LoginResponseDTO;
import com.capstone.pickIt.api.user.dto.response.OnboardingStatusResponseDTO;
import com.capstone.pickIt.api.user.dto.response.UserResponseDTO;
import com.capstone.pickIt.api.user.service.AuthService;
import com.capstone.pickIt.api.user.service.EmailService;
import com.capstone.pickIt.api.user.service.OnboardingService;
import com.capstone.pickIt.api.user.service.UserService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "유저 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final AuthService authService;
    private final OnboardingService onboardingService;

    @Operation(summary = "이메일 인증코드 전송", description = "대학교 이메일로 인증코드를 전송합니다.")
    @PostMapping("/email/send")
    public ApiResponse<Void> sendVerificationCode(@RequestBody @Valid EmailSendRequestDTO request) {
        emailService.sendVerificationCode(request);
        return ApiResponse.onSuccess(SuccessCode.OK, null);
    }

    @Operation(summary = "이메일 인증코드 확인", description = "전송된 인증코드를 검증합니다.")
    @PostMapping("/email/verify")
    public ApiResponse<Void> verifyCode(@RequestBody @Valid EmailVerifyRequestDTO request) {
        emailService.verifyCode(request);
        return ApiResponse.onSuccess(SuccessCode.OK, null);
    }

    @Operation(summary = "회원가입", description = "이메일 인증 완료 후 회원가입을 진행합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public ApiResponse<UserResponseDTO> signUp(@RequestBody @Valid UserRequestDTO request) {
        UserResponseDTO response = userService.signUp(request);
        return ApiResponse.onSuccess(SuccessCode.CREATED, response);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 Access Token을 재발급합니다.")
    @PostMapping("/refresh")
    public ApiResponse<LoginResponseDTO> refresh(@RequestBody @Valid TokenRefreshRequestDTO request) {
        LoginResponseDTO response = authService.refresh(request);
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @Operation(
            summary = "로그아웃",
            description = "Access Token을 무효화하고 Refresh Token을 삭제합니다."
    )
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        authService.logout(request.getHeader("Authorization"));
        return ApiResponse.onSuccess(SuccessCode.OK, null);
    }

    @Operation(summary = "온보딩 상태 조회", description = "로그인된 사용자의 온보딩 진행 상태를 조회합니다.")
    @GetMapping("/onboarding/status")
    public ApiResponse<OnboardingStatusResponseDTO> getOnboardingStatus() {
        Long userId = SecurityUtil.requireUserId();
        return ApiResponse.onSuccess(SuccessCode.OK, onboardingService.getOnboardingStatus(userId));
    }

    @Operation(summary = "회원 탈퇴", description = "계정을 비활성화합니다. 탈퇴 후 30일 뒤 계정이 완전히 삭제됩니다.")
    @DeleteMapping("/delete")
    public ApiResponse<String> withdraw(HttpServletRequest request) {
        Long userId = SecurityUtil.requireUserId();
        userService.withdrawUser(userId);
        authService.logout(request.getHeader("Authorization"));
        return ApiResponse.onSuccess(SuccessCode.OK, "회원 탈퇴가 완료되었습니다.");
    }

    @Operation(summary = "비밀번호 재설정 인증코드 전송", description = "가입된 이메일로 비밀번호 재설정 인증코드를 전송합니다.")
    @PostMapping("/password/reset/send")
    public ApiResponse<Void> sendPasswordResetCode(@RequestBody @Valid PasswordResetSendRequestDTO request) {
        emailService.sendPasswordResetCode(request);
        return ApiResponse.onSuccess(SuccessCode.OK, null);
    }

    @Operation(summary = "비밀번호 재설정 인증코드 확인", description = "비밀번호 재설정 인증코드를 검증합니다.")
    @PostMapping("/password/reset/verify")
    public ApiResponse<Void> verifyPasswordResetCode(@RequestBody @Valid PasswordResetVerifyRequestDTO request) {
        emailService.verifyPasswordResetCode(request);
        return ApiResponse.onSuccess(SuccessCode.OK, null);
    }

    @Operation(summary = "비밀번호 재설정", description = "인증 완료 후 새 비밀번호로 변경합니다.")
    @PutMapping("/password/reset")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid PasswordResetRequestDTO request) {
        userService.resetPassword(request);
        return ApiResponse.onSuccess(SuccessCode.OK, null);
    }

    @Operation(summary = "온보딩 기본 정보 저장", description = "학교, 학과, 학년, 학기, 수강 강의를 저장합니다.")
    @PostMapping("/onboarding/profile")
    public ApiResponse<String> saveBasicInfo(@RequestBody @Valid OnboardingBasicInfoRequestDTO request) {
        Long userId = SecurityUtil.requireUserId();
        onboardingService.saveBasicInfo(userId, request);
        return ApiResponse.onSuccess(SuccessCode.OK, "기본 정보가 저장되었습니다.");
    }

    @Operation(
            summary = "온보딩 팀플 성향 저장",
            description = "팀플 성향 선택값을 저장합니다.\n\n" +
                    "- 4개 스탭의 선택을 모두 완료한 후 **한 번에** 전송해야 합니다.\n" +
                    "- 스탭마다 개별 호출 시 이전 선택이 삭제됩니다.\n" +
                    "- `traitItemId`: GET /api/traits 로 조회한 성향 항목 ID\n" +
                    "- `selectedType`: nameA 선택 → A, nameB 선택 → B"
    )
    @PostMapping("/onboarding/personality")
    public ApiResponse<String> savePersonality(@RequestBody @Valid OnboardingPersonalityRequestDTO request) {
        Long userId = SecurityUtil.requireUserId();
        onboardingService.savePersonality(userId, request);
        return ApiResponse.onSuccess(SuccessCode.OK, "팀플 성향 입력이 완료되었습니다.");
    }
}