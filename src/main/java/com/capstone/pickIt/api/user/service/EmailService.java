package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.request.EmailSendRequestDTO;
import com.capstone.pickIt.api.user.dto.request.EmailVerifyRequestDTO;

public interface EmailService {

    void sendVerificationCode(EmailSendRequestDTO request);

    void verifyCode(EmailVerifyRequestDTO request);
}