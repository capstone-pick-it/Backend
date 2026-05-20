package com.capstone.pickIt.api.course.service;

import com.capstone.pickIt.api.course.dto.request.ProfileCreateRequestDTO;
import com.capstone.pickIt.api.course.dto.request.ProfileStatusUpdateRequestDTO;
import com.capstone.pickIt.api.course.dto.request.ProfileUpdateRequestDTO;
import com.capstone.pickIt.api.course.dto.response.UserCourseProfileResponseDTO;
import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.domain.course.entity.ImportanceLevel;
import com.capstone.pickIt.domain.course.entity.RecruitmentStatus;
import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import com.capstone.pickIt.domain.course.exception.ProfileErrorCode;
import com.capstone.pickIt.domain.course.exception.ProfileException;
import com.capstone.pickIt.domain.course.repository.CourseRepository;
import com.capstone.pickIt.domain.course.repository.UserCourseProfileRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.exception.UserErrorCode;
import com.capstone.pickIt.domain.user.exception.UserException;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCourseProfileService {

    private final UserCourseProfileRepository userCourseProfileRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<UserCourseProfileResponseDTO> getProfiles(Long userId) {
        return userCourseProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(UserCourseProfileResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserCourseProfileResponseDTO getProfile(Long userId, Long profileId) {
        UserCourseProfile profile = findAndValidateAccess(userId, profileId);
        return UserCourseProfileResponseDTO.from(profile);
    }

    @Transactional
    public UserCourseProfileResponseDTO createProfile(Long userId, ProfileCreateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ProfileException(ProfileErrorCode.COURSE_NOT_FOUND));

        if (userCourseProfileRepository.existsByUserIdAndCourseId(userId, request.getCourseId())) {
            throw new ProfileException(ProfileErrorCode.PROFILE_ALREADY_EXISTS);
        }

        ImportanceLevel importanceLevel = (request.getImportanceLevel() != null)
                ? ImportanceLevel.valueOf(request.getImportanceLevel())
                : ImportanceLevel.HIGH;

        UserCourseProfile profile = UserCourseProfile.builder()
                .user(user)
                .course(course)
                .importanceLevel(importanceLevel)
                .build();

        return UserCourseProfileResponseDTO.from(userCourseProfileRepository.save(profile));
    }

    @Transactional
    public UserCourseProfileResponseDTO updateProfile(Long userId, Long profileId, ProfileUpdateRequestDTO request) {
        UserCourseProfile profile = findAndValidateAccess(userId, profileId);
        profile.updateImportanceLevel(ImportanceLevel.valueOf(request.getImportanceLevel()));
        return UserCourseProfileResponseDTO.from(profile);
    }

    @Transactional
    public void deleteProfile(Long userId, Long profileId) {
        UserCourseProfile profile = findAndValidateAccess(userId, profileId);
        profile.softDelete();
    }

    @Transactional
    public UserCourseProfileResponseDTO updateStatus(Long userId, Long profileId, ProfileStatusUpdateRequestDTO request) {
        UserCourseProfile profile = findAndValidateAccess(userId, profileId);
        profile.changeRecruitmentStatus(RecruitmentStatus.valueOf(request.getRecruitmentStatus()));
        return UserCourseProfileResponseDTO.from(profile);
    }

    private UserCourseProfile findAndValidateAccess(Long userId, Long profileId) {
        UserCourseProfile profile = userCourseProfileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileException(ProfileErrorCode.PROFILE_NOT_FOUND));

        if (profile.isDeleted()) {
            throw new ProfileException(ProfileErrorCode.PROFILE_NOT_FOUND);
        }

        if (!profile.getUser().getId().equals(userId)) {
            throw new ProfileException(ProfileErrorCode.PROFILE_ACCESS_DENIED);
        }

        return profile;
    }
}
