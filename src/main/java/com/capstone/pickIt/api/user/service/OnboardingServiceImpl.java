package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.response.OnboardingStatusResponseDTO;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.exception.UserErrorCode;
import com.capstone.pickIt.domain.user.exception.UserException;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public OnboardingStatusResponseDTO getOnboardingStatus(Long userId) { //유저 조회 후 온보딩 상태 반환
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return OnboardingStatusResponseDTO.builder()
                .completed(user.isOnboardingCompleted())
                .currentStep(user.getOnboardingStep())
                .build();
    }
}
