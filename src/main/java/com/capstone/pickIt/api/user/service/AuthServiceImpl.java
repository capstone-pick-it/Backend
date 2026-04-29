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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh:token:";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-expire-ms}")
    private long refreshTokenExpireMs;


    /**
     * 사용자 로그인을 처리합니다.
     * 1. 사용자 존재 여부 및 비밀번호 검증
     * 2. Access/Refresh 토큰 생성
     * 3. Refresh 토큰을 Redis에 저장 (추후 갱신 및 로그아웃용)
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        //이메일로 사용자 조회하기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        //jwt 토큰 발급
        String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId(), user.getEmail());

        // refresh 토큰을 레디스에 저장
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
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    /**
     * Access 토큰 만료 시 Refresh 토큰을 통해 새로운 토큰 쌍을 발급합니다.
     * Refresh Token Rotation 전략을 사용하여 보안을 강화합니다.
     */
    @Override
    public LoginResponseDTO refresh(TokenRefreshRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        //전달 받은 refresh 토큰의 유효성 검증하기
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new UserException(UserErrorCode.TOKEN_INVALID);
        }

        // 토큰에서 유저 정보 추출하기
        Long userId = jwtProvider.getMemberId(refreshToken);
        String email = jwtProvider.getEmail(refreshToken);

        //레디스에 저장된 토큰하고 일치하는지 확인하기
        String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
        if (!refreshToken.equals(storedToken)) {
            throw new UserException(UserErrorCode.TOKEN_INVALID);
        }

        //유저 존재 여부 재확인하기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        //새로운 토큰 쌍 생성하기 기존 토큰은 무효화하고 새롭게 발급해주는 것
        String newAccessToken = jwtProvider.createAccessToken(userId, email);
        String newRefreshToken = jwtProvider.createRefreshToken(userId, email);

        //레디스에 새로운 refresh토큰 갱신하기
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + userId,
                newRefreshToken,
                refreshTokenExpireMs,
                TimeUnit.MILLISECONDS
        );

        return LoginResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
