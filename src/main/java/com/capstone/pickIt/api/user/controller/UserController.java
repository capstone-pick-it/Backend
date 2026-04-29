package com.capstone.pickIt.api.user.controller;

import com.capstone.pickIt.api.user.dto.request.EmailSendRequestDTO;
import com.capstone.pickIt.api.user.dto.request.EmailVerifyRequestDTO;
import com.capstone.pickIt.api.user.dto.request.UserRequestDTO;
import com.capstone.pickIt.api.user.dto.response.UserResponseDTO;
import com.capstone.pickIt.api.user.service.EmailService;
import com.capstone.pickIt.api.user.service.UserService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/email/send") //이메일 인증 코드 전송
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@RequestBody @Valid EmailSendRequestDTO request) {
        emailService.sendVerificationCode(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(null, SuccessCode.OK));
    }

    @PostMapping("/email/verify") // 사용자 이메일 인증 코드 확인
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody @Valid EmailVerifyRequestDTO request) {
        emailService.verifyCode(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(null, SuccessCode.OK));
    }

    @PostMapping("/signup") // 회원가입
    public ResponseEntity<ApiResponse<UserResponseDTO>> signUp(@RequestBody @Valid UserRequestDTO request) {
        UserResponseDTO response = userService.signUp(request);
        return ResponseEntity
                .status(SuccessCode.CREATED.getHttpStatus())
                .body(ApiResponse.onSuccess(response, SuccessCode.CREATED));
    }
}
