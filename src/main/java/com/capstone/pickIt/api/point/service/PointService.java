package com.capstone.pickIt.api.point.service;

import com.capstone.pickIt.api.point.dto.response.PointResponseDTO;

public interface PointService {

    PointResponseDTO getMyPoint();

    PointResponseDTO refreshAndGetPoint(Long userId);
}