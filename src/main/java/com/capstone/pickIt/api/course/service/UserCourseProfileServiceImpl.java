package com.capstone.pickIt.api.course.service;

import com.capstone.pickIt.api.course.dto.request.ProfileCreateRequestDTO;
import com.capstone.pickIt.api.course.dto.request.ProfileStatusUpdateRequestDTO;
import com.capstone.pickIt.api.course.dto.request.ProfileUpdateRequestDTO;
import com.capstone.pickIt.api.course.dto.request.ProfileUpdateRequestDTO.TraitUpdateDTO;
import com.capstone.pickIt.api.course.dto.response.UserCourseProfileResponseDTO;
import com.capstone.pickIt.domain.course.entity.*;
import com.capstone.pickIt.domain.course.exception.ProfileErrorCode;
import com.capstone.pickIt.domain.course.exception.ProfileException;
import com.capstone.pickIt.domain.course.repository.CourseRepository;
import com.capstone.pickIt.domain.course.repository.UserCourseProfileRepository;
import com.capstone.pickIt.domain.course.repository.UserCourseTraitRepository;
import com.capstone.pickIt.domain.trait.entity.TraitItem;
import com.capstone.pickIt.domain.trait.entity.TraitSide;
import com.capstone.pickIt.domain.trait.exception.TraitErrorCode;
import com.capstone.pickIt.domain.trait.exception.TraitException;
import com.capstone.pickIt.domain.trait.repository.TraitItemRepository;
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
@Transactional(readOnly = true)
public class UserCourseProfileServiceImpl implements UserCourseProfileService {

    private final UserCourseProfileRepository userCourseProfileRepository;
    private final UserCourseTraitRepository userCourseTraitRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final TraitItemRepository traitItemRepository;

    @Override
    public List<UserCourseProfileResponseDTO> getProfiles(Long userId) {
        return userCourseProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(UserCourseProfileResponseDTO::from)
                .toList();
    }

    @Override
    public UserCourseProfileResponseDTO getProfile(Long userId, Long profileId) {
        UserCourseProfile profile = findAndValidateAccess(userId, profileId);
        return UserCourseProfileResponseDTO.from(profile);
    }

    @Override
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

    @Override
    @Transactional
    public UserCourseProfileResponseDTO updateProfile(Long userId, Long profileId, ProfileUpdateRequestDTO request) {
        UserCourseProfile profile = findAndValidateAccess(userId, profileId);

        if (request.getImportanceLevel() != null) {
            profile.updateImportanceLevel(ImportanceLevel.valueOf(request.getImportanceLevel()));
        }

        if (request.getTraits() != null) {
            List<TraitUpdateDTO> traits = request.getTraits();

            List<Long> traitItemIds = traits.stream().map(TraitUpdateDTO::getTraitItemsId).toList();
            if (traitItemIds.size() != traitItemIds.stream().distinct().count()) {
                throw new TraitException(TraitErrorCode.DUPLICATE_TRAIT_ITEM);
            }

            userCourseTraitRepository.deleteByUserCourseProfileId(profileId);

            List<UserCourseTrait> newTraits = traits.stream()
                    .map(dto -> {
                        TraitItem traitItem = traitItemRepository.findById(dto.getTraitItemsId())
                                .orElseThrow(() -> new TraitException(TraitErrorCode.TRAIT_NOT_FOUND));
                        return UserCourseTrait.builder()
                                .userCourseProfile(profile)
                                .traitItem(traitItem)
                                .selectedSide(TraitSide.valueOf(dto.getSelectedSide()))
                                .build();
                    })
                    .toList();

            userCourseTraitRepository.saveAll(newTraits);
        }

        return UserCourseProfileResponseDTO.from(profile);
    }

    @Override
    @Transactional
    public void deleteProfile(Long userId, Long profileId) {
        UserCourseProfile profile = findAndValidateAccess(userId, profileId);
        profile.softDelete();
    }

    @Override
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
