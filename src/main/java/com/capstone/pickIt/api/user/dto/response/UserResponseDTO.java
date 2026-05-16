package com.capstone.pickIt.api.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDTO {

    private Long userId;
    private String email;
    private String nickname;

}