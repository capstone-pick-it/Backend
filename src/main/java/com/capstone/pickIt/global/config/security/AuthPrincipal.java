package com.capstone.pickIt.global.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.Principal;

@Getter
@AllArgsConstructor
public class AuthPrincipal{
    private final Long userId;
    private final String email;
}
