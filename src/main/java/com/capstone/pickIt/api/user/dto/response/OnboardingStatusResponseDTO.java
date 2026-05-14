package com.capstone.pickIt.api.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OnboardingStatusResponseDTO {

    @JsonProperty("isCompleted")
    private final boolean completed;

    private final int currentStep;
}