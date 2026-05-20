package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.request.EmailSendRequestDTO;
import com.capstone.pickIt.api.user.dto.request.EmailVerifyRequestDTO;
import com.capstone.pickIt.api.user.dto.request.PasswordResetSendRequestDTO;
import com.capstone.pickIt.api.user.dto.request.PasswordResetVerifyRequestDTO;

public interface EmailService {

    void sendVerificationCode(EmailSendRequestDTO request);

    void verifyCode(EmailVerifyRequestDTO request);

    void sendPasswordResetCode(PasswordResetSendRequestDTO request);

    void verifyPasswordResetCode(PasswordResetVerifyRequestDTO request);
}