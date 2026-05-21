package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.response.CourseCardResponseDTO;
import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import com.capstone.pickIt.domain.course.repository.UserCourseProfileRepository;
import com.capstone.pickIt.domain.course.repository.UserCourseTraitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageCourseService {

    private final UserCourseProfileRepository userCourseProfileRepository;
    private final UserCourseTraitRepository userCourseTraitRepository;

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
}