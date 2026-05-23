package com.capstone.pickIt.api.user.dto.response;

public record MyPageProfileResponseDTO(
        String nickname,
        String major,
        Integer grade,
        int teamLevel,
        int point
) {
}