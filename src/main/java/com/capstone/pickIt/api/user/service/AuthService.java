package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.request.LoginRequestDTO;
import com.capstone.pickIt.api.user.dto.request.TokenRefreshRequestDTO;
import com.capstone.pickIt.api.user.dto.response.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    LoginResponseDTO refresh(TokenRefreshRequestDTO request);
}