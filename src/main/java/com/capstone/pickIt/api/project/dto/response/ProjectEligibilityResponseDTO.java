package com.capstone.pickIt.api.project.dto.response;

public record ProjectEligibilityResponseDTO(
        Long userId,
        Boolean eligible,
        Integer balance,
        Integer requiredPoint,
        Integer shortagePoint,
        String reason
) {
}