package com.capstone.pickIt.api.course.service;

import com.capstone.pickIt.api.course.dto.request.ProfileCreateRequestDTO;
import com.capstone.pickIt.api.course.dto.request.ProfileStatusUpdateRequestDTO;
import com.capstone.pickIt.api.course.dto.request.ProfileUpdateRequestDTO;
import com.capstone.pickIt.api.course.dto.response.UserCourseProfileResponseDTO;

import java.util.List;

public interface UserCourseProfileService {

    List<UserCourseProfileResponseDTO> getProfiles(Long userId);

    UserCourseProfileResponseDTO getProfile(Long userId, Long profileId);

    UserCourseProfileResponseDTO createProfile(Long userId, ProfileCreateRequestDTO request);

    UserCourseProfileResponseDTO updateProfile(Long userId, Long profileId, ProfileUpdateRequestDTO request);

    void deleteProfile(Long userId, Long profileId);

    UserCourseProfileResponseDTO updateStatus(Long userId, Long profileId, ProfileStatusUpdateRequestDTO request);
}
