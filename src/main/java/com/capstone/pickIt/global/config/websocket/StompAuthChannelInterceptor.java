package com.capstone.pickIt.global.config.websocket;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;
import com.capstone.pickIt.global.apiPayload.response.ErrorCode;
import com.capstone.pickIt.global.config.security.AuthPrincipal;
import com.capstone.pickIt.global.infra.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authorization = accessor.getFirstNativeHeader("Authorization");

            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            if (!jwtProvider.validateAccessToken(authorization)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            Long userId = jwtProvider.getUserId(authorization);
            String email = jwtProvider.getEmail(authorization);

            accessor.setUser(() -> userId.toString());

            Map<String, Object> sessionAttrs = accessor.getSessionAttributes();

            if (sessionAttrs != null) {
                sessionAttrs.put("userId", userId);
                sessionAttrs.put("email", email);
            }
        }

        return message;
    }
}
