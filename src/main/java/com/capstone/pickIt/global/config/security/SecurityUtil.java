package com.capstone.pickIt.global.config.security;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;
import com.capstone.pickIt.global.apiPayload.response.ErrorCode;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {}

    public static AuthPrincipal currentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof AuthPrincipal authPrincipal) {
            return authPrincipal;
        }

        return null;
    }

    public static Long currentUserId() {
        AuthPrincipal principal = currentPrincipal();
        return principal != null ? principal.getUserId() : null;
    }

    public static Long requireUserId() {
        Long userId = currentUserId();
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return userId;
    }

    public static String currentEmail() {
        AuthPrincipal principal = currentPrincipal();
        return principal != null ? principal.getEmail() : null;
    }
}
