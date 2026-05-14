package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.response.OnboardingStatusResponseDTO;

public interface OnboardingService {

    OnboardingStatusResponseDTO getOnboardingStatus(Long userId);
}