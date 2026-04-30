package com.capstone.pickIt.api.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRefreshRequestDTO {

    @NotBlank
    private String refreshToken;
}