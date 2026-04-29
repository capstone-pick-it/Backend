package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.request.EmailSendRequestDTO;
import com.capstone.pickIt.api.user.dto.request.EmailVerifyRequestDTO;
import com.capstone.pickIt.domain.user.exception.UserErrorCode;
import com.capstone.pickIt.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String EMAIL_CODE_PREFIX = "email:code:";
    private static final String EMAIL_VERIFIED_PREFIX = "email:verified:";
    private static final long CODE_TTL_MINUTES = 5;
    private static final long VERIFIED_TTL_MINUTES = 30;

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    public void sendVerificationCode(EmailSendRequestDTO request) {
        String email = request.getEmail();
        String code = generateCode();

        redisTemplate.opsForValue().set(
                EMAIL_CODE_PREFIX + email,
                code,
                CODE_TTL_MINUTES,
                TimeUnit.MINUTES
        );

        sendEmail(email, code);
    }

    public void verifyCode(EmailVerifyRequestDTO request) {
        String email = request.getEmail();
        String storedCode = redisTemplate.opsForValue().get(EMAIL_CODE_PREFIX + email);

        if (storedCode == null || !storedCode.equals(request.getCode())) {
            throw new CustomException(UserErrorCode.EMAIL_CODE_INVALID);
        }

        redisTemplate.delete(EMAIL_CODE_PREFIX + email);
        redisTemplate.opsForValue().set(
                EMAIL_VERIFIED_PREFIX + email,
                "true",
                VERIFIED_TTL_MINUTES,
                TimeUnit.MINUTES
        );
    }

    @Async
    protected void sendEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[PickIt] 이메일 인증 코드");
        message.setText(
                "안녕하세요, PickIt입니다.\n\n" +
                "인증 코드: " + code + "\n\n" +
                "유효 시간은 5분입니다.\n" +
                "본인이 요청하지 않은 경우 이 메일을 무시해주세요."
        );
        mailSender.send(message);
    }

    private String generateCode() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }
}