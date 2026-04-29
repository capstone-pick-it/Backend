package com.capstone.pickIt.api.user.dto.request;

import lombok.Getter;

@Getter
public class UserRequestDTO {

    private String email;
    private String password;
    private String nickname;
    private String major;
}