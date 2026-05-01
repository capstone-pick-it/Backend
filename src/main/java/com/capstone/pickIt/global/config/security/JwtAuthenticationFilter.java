package com.capstone.pickIt.global.config.security;

import com.capstone.pickIt.global.infra.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "blacklist:access:";

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {

        // 이미 인증이 세팅되어 있으면 스킵 (중복 세팅 방지)
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader= request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);

            boolean isBlacklisted = Boolean.TRUE.equals(
                    redisTemplate.hasKey(ACCESS_TOKEN_BLACKLIST_PREFIX + token));

            if(jwtProvider.validateAccessToken(token) && !isBlacklisted) {

                Long memberId = jwtProvider.getMemberId(token);
                String email = jwtProvider.getEmail(token);

                AuthPrincipal principal = new AuthPrincipal(memberId, email);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                principal, null, Collections.emptyList()
                        );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
