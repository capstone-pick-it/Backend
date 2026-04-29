package com.capstone.pickIt.api.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailSendRequestDTO {

    @NotBlank
    @Email
    private String email;
}