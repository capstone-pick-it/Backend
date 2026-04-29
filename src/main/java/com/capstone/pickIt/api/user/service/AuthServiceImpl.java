package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.request.LoginRequestDTO;
import com.capstone.pickIt.api.user.dto.request.TokenRefreshRequestDTO;
import com.capstone.pickIt.api.user.dto.response.LoginResponseDTO;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.exception.UserErrorCode;
import com.capstone.pickIt.domain.user.exception.UserException;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import com.capstone.pickIt.global.infra.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh:token:";

    // Refresh Token Rotation을 원자적으로 처리하는 Lua 스크립트
    // 기존 토큰과 일치할 때만 새 토큰으로 교체 (Race Condition 방지)
    private static final DefaultRedisScript<Long> REFRESH_TOKEN_ROTATION_SCRIPT =
            new DefaultRedisScript<>(
                    "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "   redis.call('set', KEYS[1], ARGV[2], 'PX', ARGV[3]) " +
                    "   return 1 " +
                    "else " +
                    "   return 0 " +
                    "end",
                    Long.class
            );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-expire-ms}")
    private long refreshTokenExpireMs;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId(), user.getEmail());

        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + user.getUserId(),
                refreshToken,
                refreshTokenExpireMs,
                TimeUnit.MILLISECONDS
        );

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .build();
    }

    @Override
    public LoginResponseDTO refresh(TokenRefreshRequestDTO request) {
        //  Bearer 접두사 제거 후 정규화된 토큰으로 이후 모든 작업 수행
        String refreshToken = jwtProvider.normalize(request.getRefreshToken());

        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new UserException(UserErrorCode.TOKEN_INVALID);
        }

        Long userId = jwtProvider.getMemberId(refreshToken);

        // 변경 불가능한 userId로 사용자 조회 → 최신 이메일로 새 토큰 생성
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtProvider.createAccessToken(user.getUserId(), user.getEmail());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getUserId(), user.getEmail());

        // Lua 스크립트로 기존 토큰 검증 + 새 토큰 저장을 원자적으로 처리
        Long result = redisTemplate.execute(
                REFRESH_TOKEN_ROTATION_SCRIPT,
                Collections.singletonList(REFRESH_TOKEN_PREFIX + userId),
                refreshToken,
                newRefreshToken,
                String.valueOf(refreshTokenExpireMs)
        );

        if (result == null || result == 0L) {
            throw new UserException(UserErrorCode.TOKEN_INVALID);
        }

        return LoginResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .build();
    }
}
