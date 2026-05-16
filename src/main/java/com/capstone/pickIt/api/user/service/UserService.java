package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.request.UserRequestDTO;
import com.capstone.pickIt.api.user.dto.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO signUp(UserRequestDTO request);

    void withdrawUser(Long userId);
}