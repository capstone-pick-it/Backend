package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.request.PasswordResetRequestDTO;
import com.capstone.pickIt.api.user.dto.request.UserRequestDTO;
import com.capstone.pickIt.api.user.dto.response.UserResponseDTO;
import com.capstone.pickIt.domain.point.entity.Point;
import com.capstone.pickIt.domain.point.entity.PointTransaction;
import com.capstone.pickIt.domain.point.entity.PointTransactionType;
import com.capstone.pickIt.domain.point.repository.PointRepository;
import com.capstone.pickIt.domain.point.repository.PointTransactionRepository;
import com.capstone.pickIt.domain.teamlevel.entity.TeamLevel;
import com.capstone.pickIt.domain.teamlevel.repository.TeamLevelRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.exception.UserErrorCode;
import com.capstone.pickIt.domain.user.exception.UserException;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String EMAIL_VERIFIED_PREFIX = "email:verified:";
    private static final String PASSWORD_RESET_VERIFIED_PREFIX = "password:reset:verified:";
    static final String WITHDRAWN_USER_PREFIX = "withdrawn:user:";
    private static final int SIGNUP_REWARD_AMOUNT = 100;

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final TeamLevelRepository teamLevelRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public void resetPassword(PasswordResetRequestDTO request) {
        String email = request.getEmail();

        if (!Boolean.TRUE.equals(redisTemplate.hasKey(PASSWORD_RESET_VERIFIED_PREFIX + email))) {
            throw new UserException(UserErrorCode.PASSWORD_RESET_NOT_VERIFIED);
        }

        if (!userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }

        userRepository.updatePasswordByEmail(email, passwordEncoder.encode(request.getNewPassword()));

        redisTemplate.delete(PASSWORD_RESET_VERIFIED_PREFIX + email);
    }

    @Override
    @Transactional
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (user.isWithdrawn()) {
            throw new UserException(UserErrorCode.ALREADY_WITHDRAWN);
        }

        user.withdraw();

        try {
            userRepository.flush();
        } catch (OptimisticLockingFailureException e) {
            throw new UserException(UserErrorCode.ALREADY_WITHDRAWN);
        }

        redisTemplate.opsForValue().set(WITHDRAWN_USER_PREFIX + userId, "1", Duration.ofDays(31));
    }

    @Override
    @Transactional
    public UserResponseDTO signUp(UserRequestDTO request) {
        String email = request.getEmail();

        if (!Boolean.TRUE.equals(redisTemplate.hasKey(EMAIL_VERIFIED_PREFIX + email))) {
            throw new UserException(UserErrorCode.USER_EMAIL_NOT_VERIFIED);
        }

        if (userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        User savedUser = userRepository.save(user);

        Point point = Point.builder()
                .user(savedUser)
                .balance(SIGNUP_REWARD_AMOUNT)
                .build();
        pointRepository.save(point);

        pointTransactionRepository.save(PointTransaction.builder()
                .user(savedUser)
                .transactionType(PointTransactionType.SIGNUP_REWARD)
                .amount(SIGNUP_REWARD_AMOUNT)
                .balanceAfter(SIGNUP_REWARD_AMOUNT)
                .description("회원가입 보상 포인트")
                .build());

        teamLevelRepository.save(TeamLevel.builder()
                .user(savedUser)
                .build());

        redisTemplate.delete(EMAIL_VERIFIED_PREFIX + email);

        return UserResponseDTO.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .build();
    }
}