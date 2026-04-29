package com.capstone.pickIt.api.user.controller;

import com.capstone.pickIt.api.user.dto.request.EmailSendRequestDTO;
import com.capstone.pickIt.api.user.dto.request.EmailVerifyRequestDTO;
import com.capstone.pickIt.api.user.dto.request.UserRequestDTO;
import com.capstone.pickIt.api.user.dto.response.UserResponseDTO;
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

    @Operation(summary = "이메일 인증코드 전송", description = "대학교 이메일로 인증코드를 전송합니다.")
    @PostMapping("/email/send") //이메일 인증 코드 전송
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@RequestBody @Valid EmailSendRequestDTO request) {
        emailService.sendVerificationCode(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(null, SuccessCode.OK));
    }

    @Operation(summary = "이메일 인증코드 확인", description = "전송된 인증코드를 검증합니다.")
    @PostMapping("/email/verify") // 사용자 이메일 인증 코드 확인
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody @Valid EmailVerifyRequestDTO request) {
        emailService.verifyCode(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(null, SuccessCode.OK));
    }

    @Operation(summary = "회원가입", description = "이메일 인증 완료 후 회원가입을 진행합니다.")
    @PostMapping("/signup") // 회원가입
    public ResponseEntity<ApiResponse<UserResponseDTO>> signUp(@RequestBody @Valid UserRequestDTO request) {
        UserResponseDTO response = userService.signUp(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponseDTO>builder()
                        .isSuccess(true)
                        .code(SuccessCode.CREATED.getCode())
                        .message("회원가입에 성공했습니다.")
                        .result(response)
                        .build());
    }
}
