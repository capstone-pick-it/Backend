package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.response.MyPageProfileResponseDTO;

public interface MypageProfileService {

    MyPageProfileResponseDTO getProfile(Long userId);
}