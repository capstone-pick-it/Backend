package com.capstone.pickIt.api.course.service;

import com.capstone.pickIt.api.course.dto.response.RecruitProfileResponseDTO;
import com.capstone.pickIt.domain.course.entity.UserCourseTrait;
import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import com.capstone.pickIt.domain.course.repository.UserCourseProfileRepository;
import com.capstone.pickIt.domain.course.repository.UserCourseTraitRepository;
import com.capstone.pickIt.domain.trait.entity.TraitSide;
import com.capstone.pickIt.domain.user.entity.UserDefaultTrait;
import com.capstone.pickIt.domain.user.repository.UserDefaultTraitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final UserDefaultTraitRepository userDefaultTraitRepository;
    private final UserCourseProfileRepository userCourseProfileRepository;
    private final UserCourseTraitRepository userCourseTraitRepository;

    /**
     * 모집 탭 카드 목록 조회 (성향 유사순 정렬)
     * <p>
     * 1. 내 UserDefaultTrait 조회 → Map<traitItemsId, selectedSide>로 변환
     * 2. courseId로 RECRUITING 상태이고 내 카드 제외한 카드들 조회
     * 3. 각 카드마다 성향 일치 개수(traitScore) 계산
     * 4. traitScore 높은 순으로 정렬해서 반환
     */
    @Transactional(readOnly = true)
    public List<RecruitProfileResponseDTO> getRecruitProfiles(Long userId, Long courseId) {

        // 1. 내 UserDefaultTrait 조회 → Map<traitItemsId, selectedSide>
        List<UserDefaultTrait> myDefaultTraits = userDefaultTraitRepository.findByUserId(userId);
        Map<Long, TraitSide> myTraitMap = myDefaultTraits.stream()
                .collect(Collectors.toMap(
                        t -> t.getTraitItem().getTraitItemsId(),
                        UserDefaultTrait::getSelectedSide
                ));

        // 2. courseId로 RECRUITING 상태이고 내 카드 제외한 카드들 조회
        List<UserCourseProfile> profiles =
                userCourseProfileRepository.findRecruitingProfilesExcludingUser(courseId, userId);

        if (profiles.isEmpty()) {
            return List.of();
        }

        // 3. 해당 카드들의 성향 정보를 한 번에 조회 (N+1 방지)
        List<Long> profileIds = profiles.stream()
                .map(UserCourseProfile::getId)
                .toList();

        List<UserCourseTrait> allTraits =
                userCourseTraitRepository.findAllByUserCourseProfileIdIn(profileIds);

        // profileId → traits 매핑
        Map<Long, List<UserCourseTrait>> traitsByProfileId = allTraits.stream()
                .collect(Collectors.groupingBy(t -> t.getUserCourseProfile().getId()));

        // 4. 각 카드마다 traitScore 계산 후 DTO 변환, score 높은 순 정렬
        return profiles.stream()
                .map(profile -> {
                    List<UserCourseTrait> profileTraits =
                            traitsByProfileId.getOrDefault(profile.getId(), List.of());

                    int traitScore = (int) profileTraits.stream()
                            .filter(t -> {
                                TraitSide mySide = myTraitMap.get(t.getTraitItem().getTraitItemsId());
                                return mySide != null && mySide == t.getSelectedSide();
                            })
                            .count();

                    List<RecruitProfileResponseDTO.TraitDTO> traitDTOs = profileTraits.stream()
                            .map(t -> RecruitProfileResponseDTO.TraitDTO.builder()
                                    .traitItemsId(t.getTraitItem().getTraitItemsId())
                                    .nameA(t.getTraitItem().getNameA())
                                    .nameB(t.getTraitItem().getNameB())
                                    .selectedSide(t.getSelectedSide().name())
                                    .build())
                            .toList();

                    return RecruitProfileResponseDTO.builder()
                            .userCourseProfileId(profile.getId())
                            .userId(profile.getUser().getId())
                            .nickname(profile.getUser().getNickname())
                            .school(profile.getUser().getSchool())
                            .major(profile.getUser().getMajor())
                            .grade(profile.getUser().getGrade())
                            .courseId(profile.getCourse().getId())
                            .courseName(profile.getCourse().getCourseName())
                            .semester(profile.getCourse().getSemester())
                            .importanceLevel(profile.getImportanceLevel().name())
                            .recruitmentStatus(profile.getRecruitmentStatus().name())
                            .traitScore(traitScore)
                            .traits(traitDTOs)
                            .build();
                })
                .sorted(Comparator.comparingInt(RecruitProfileResponseDTO::getTraitScore).reversed())
                .toList();
    }
}
