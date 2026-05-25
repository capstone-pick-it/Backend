package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.request.AddCourseRequestDTO;
import com.capstone.pickIt.api.user.dto.request.UpdateCourseRequestDTO;
import com.capstone.pickIt.api.user.dto.response.CourseCardResponseDTO;
import com.capstone.pickIt.api.user.dto.response.CourseListResponseDTO;
import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.domain.course.entity.RecruitmentStatus;
import com.capstone.pickIt.domain.course.entity.UserCourse;
import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import com.capstone.pickIt.domain.course.entity.UserCourseTrait;
import com.capstone.pickIt.domain.course.exception.ProfileErrorCode;
import com.capstone.pickIt.domain.course.exception.ProfileException;
import com.capstone.pickIt.domain.course.repository.CourseRepository;
import com.capstone.pickIt.domain.course.repository.UserCourseProfileRepository;
import com.capstone.pickIt.domain.course.repository.UserCourseRepository;
import com.capstone.pickIt.domain.course.repository.UserCourseTraitRepository;
import com.capstone.pickIt.domain.trait.entity.TraitItem;
import com.capstone.pickIt.domain.trait.exception.TraitErrorCode;
import com.capstone.pickIt.domain.trait.exception.TraitException;
import com.capstone.pickIt.domain.trait.repository.TraitItemRepository;
import com.capstone.pickIt.domain.project.entity.ProjectTeamStatus;
import com.capstone.pickIt.domain.project.repository.ProjectTeamMemberRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import com.capstone.pickIt.global.apiPayload.response.ErrorCode;
import com.capstone.pickIt.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageCourseService {

    private final UserCourseProfileRepository userCourseProfileRepository;
    private final UserCourseTraitRepository userCourseTraitRepository;
    private final UserCourseRepository userCourseRepository;
    private final CourseRepository courseRepository;
    private final TraitItemRepository traitItemRepository;
    private final UserRepository userRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;

    @Transactional(readOnly = true)
    public List<CourseListResponseDTO> getCourseList(Long userId) {
        return userCourseProfileRepository
                .findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)
                .stream()
                .map(CourseListResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseCardResponseDTO> getCourseCards(Long userId) {
        List<UserCourseProfile> profiles = userCourseProfileRepository
                .findAllByUserIdAndDeletedAtIsNull(userId);

        return profiles.stream()
                .map(profile -> CourseCardResponseDTO.from(
                        profile,
                        userCourseTraitRepository.findByUserCourseProfileId(profile.getId())
                ))
                .toList();
    }

    @Transactional
    public void updateCourse(Long userId, Long courseId, UpdateCourseRequestDTO request) {
        UserCourseProfile profile = userCourseProfileRepository
                .findByUserIdAndCourseIdAndDeletedAtIsNull(userId, courseId)
                .orElseThrow(() -> new ProfileException(ProfileErrorCode.PROFILE_NOT_FOUND));

        profile.updateImportanceLevel(request.getImportance());

        userCourseTraitRepository.deleteByUserCourseProfileId(profile.getId());

        for (UpdateCourseRequestDTO.TraitDTO traitDTO : request.getTraits()) {
            TraitItem traitItem = traitItemRepository.findById(traitDTO.getTraitItemId())
                    .orElseThrow(() -> new TraitException(TraitErrorCode.TRAIT_NOT_FOUND));

            userCourseTraitRepository.save(
                    UserCourseTrait.builder()
                            .userCourseProfile(profile)
                            .traitItem(traitItem)
                            .selectedSide(traitDTO.getSelectedType())
                            .build()
            );
        }
    }

    @Transactional
    public void deleteCourse(Long userId, Long courseId) {
        UserCourseProfile profile = userCourseProfileRepository
                .findByUserIdAndCourseIdAndDeletedAtIsNull(userId, courseId)
                .orElseThrow(() -> new ProfileException(ProfileErrorCode.PROFILE_NOT_FOUND));

        boolean hasActiveTeam = projectTeamMemberRepository.existsActiveTeamByCourseAndUser(
                courseId, userId,
                List.of(ProjectTeamStatus.RECRUITING, ProjectTeamStatus.IN_PROGRESS)
        );
        if (hasActiveTeam) {
            throw new ProfileException(ProfileErrorCode.COURSE_HAS_ACTIVE_TEAM);
        }

        userCourseTraitRepository.deleteByUserCourseProfileId(profile.getId());
        userCourseRepository.deleteByUserIdAndCourseId(userId, courseId);
        profile.softDelete();
    }

    @Transactional
    public void addCourse(Long userId, AddCourseRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Course course = courseRepository
                .findByCourseNameAndSemester(request.getCourseName(), request.getSemester())
                .orElseGet(() -> courseRepository.save(
                        Course.builder()
                                .courseName(request.getCourseName())
                                .semester(request.getSemester())
                                .build()
                ));

        if (userCourseProfileRepository.existsByUserIdAndCourseIdAndDeletedAtIsNull(userId, course.getId())) {
            throw new ProfileException(ProfileErrorCode.PROFILE_ALREADY_EXISTS);
        }

        if (!userCourseRepository.existsByUserIdAndCourseId(userId, course.getId())) {
            userCourseRepository.save(
                    UserCourse.builder()
                            .user(user)
                            .course(course)
                            .build()
            );
        }

        UserCourseProfile profile = userCourseProfileRepository.save(
                UserCourseProfile.builder()
                        .user(user)
                        .course(course)
                        .importanceLevel(request.getImportance())
                        .recruitmentStatus(RecruitmentStatus.RECRUITING)
                        .build()
        );

        for (AddCourseRequestDTO.TraitDTO traitDTO : request.getTraits()) {
            TraitItem traitItem = traitItemRepository.findById(traitDTO.getTraitItemId())
                    .orElseThrow(() -> new TraitException(TraitErrorCode.TRAIT_NOT_FOUND));

            userCourseTraitRepository.save(
                    UserCourseTrait.builder()
                            .userCourseProfile(profile)
                            .traitItem(traitItem)
                            .selectedSide(traitDTO.getSelectedType())
                            .build()
            );
        }
    }
}