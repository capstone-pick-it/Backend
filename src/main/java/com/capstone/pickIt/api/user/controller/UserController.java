package com.capstone.pickIt.api.user.controller;

import com.capstone.pickIt.api.user.dto.request.EmailSendRequestDTO;
import com.capstone.pickIt.api.user.dto.request.EmailVerifyRequestDTO;
import com.capstone.pickIt.api.user.dto.request.LoginRequestDTO;
import com.capstone.pickIt.api.user.dto.request.TokenRefreshRequestDTO;
import com.capstone.pickIt.api.user.dto.request.UserRequestDTO;
import com.capstone.pickIt.api.user.dto.response.LoginResponseDTO;
import com.capstone.pickIt.api.user.dto.response.UserResponseDTO;
import com.capstone.pickIt.api.user.service.AuthService;
import com.capstone.pickIt.api.user.service.EmailService;
import com.capstone.pickIt.api.user.service.UserService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "유저 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final AuthService authService;

    @Operation(summary = "이메일 인증코드 전송", description = "대학교 이메일로 인증코드를 전송합니다.")
    @PostMapping("/email/send")
    public ApiResponse<Void> sendVerificationCode(@RequestBody @Valid EmailSendRequestDTO request) {
        emailService.sendVerificationCode(request);
        return ApiResponse.onSuccess(null, SuccessCode.OK);
    }

    @Operation(summary = "이메일 인증코드 확인", description = "전송된 인증코드를 검증합니다.")
    @PostMapping("/email/verify")
    public ApiResponse<Void> verifyCode(@RequestBody @Valid EmailVerifyRequestDTO request) {
        emailService.verifyCode(request);
        return ApiResponse.onSuccess(null, SuccessCode.OK);
    }

    @Operation(summary = "회원가입", description = "이메일 인증 완료 후 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDTO>> signUp(@RequestBody @Valid UserRequestDTO request) {
        UserResponseDTO response = userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponseDTO>builder()
                        .isSuccess(true)
                        .code(SuccessCode.CREATED.getCode())
                        .message("회원가입에 성공했습니다.")
                        .result(response)
                        .build());
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ApiResponse.<LoginResponseDTO>builder()
                .isSuccess(true)
                .code(SuccessCode.OK.getCode())
                .message("로그인에 성공했습니다.")
                .result(response)
                .build();
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 Access Token을 재발급합니다.")
    @PostMapping("/refresh")
    public ApiResponse<LoginResponseDTO> refresh(@RequestBody @Valid TokenRefreshRequestDTO request) {
        LoginResponseDTO response = authService.refresh(request);
        return ApiResponse.<LoginResponseDTO>builder()
                .isSuccess(true)
                .code(SuccessCode.OK.getCode())
                .message("토큰이 재발급되었습니다.")
                .result(response)
                .build();
    }
}