package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.request.OnboardingBasicInfoRequestDTO;
import com.capstone.pickIt.api.user.dto.request.OnboardingPersonalityRequestDTO;
import com.capstone.pickIt.api.user.dto.request.UserDefaultTraitRequestDTO;
import com.capstone.pickIt.api.user.dto.response.OnboardingStatusResponseDTO;
import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.domain.course.entity.UserCourse;
import com.capstone.pickIt.domain.course.repository.CourseRepository;
import com.capstone.pickIt.domain.course.repository.UserCourseRepository;
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
public class OnboardingServiceImpl implements OnboardingService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserDefaultTraitService userDefaultTraitService;

    @Override
    @Transactional(readOnly = true)
    public OnboardingStatusResponseDTO getOnboardingStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return OnboardingStatusResponseDTO.builder()
                .completed(user.isOnboardingCompleted())
                .currentStep(user.getOnboardingStep())
                .build();
    }

    @Override
    @Transactional
    public void saveBasicInfo(Long userId, OnboardingBasicInfoRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.updateBasicInfo(request.getSchool(), request.getMajor(), request.getGrade(), request.getSemester());

        for (String courseName : request.getCourses()) {
            Course course = courseRepository
                    .findByCourseNameAndSemester(courseName, request.getSemester())
                    .orElseGet(() -> courseRepository.save(
                            Course.builder()
                                    .courseName(courseName)
                                    .semester(request.getSemester())
                                    .build()
                    ));

            if (!userCourseRepository.existsByUserIdAndCourseId(userId, course.getId())) {
                userCourseRepository.save(
                        UserCourse.builder()
                                .user(user)
                                .course(course)
                                .build()
                );
            }
        }
    }

    @Override
    @Transactional
    public void savePersonality(Long userId, OnboardingPersonalityRequestDTO request) {
        List<UserDefaultTraitRequestDTO> traits = request.getTraits().stream()
                .map(item -> new UserDefaultTraitRequestDTO(item.getTraitItemId(), item.getSelectedType()))
                .toList();
        userDefaultTraitService.updateDefaultTraits(userId, traits);
    }
}