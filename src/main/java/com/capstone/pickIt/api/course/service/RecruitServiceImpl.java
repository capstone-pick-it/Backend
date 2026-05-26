package com.capstone.pickIt.api.course.service;

import com.capstone.pickIt.api.course.dto.response.RecruitProfileResponseDTO;
import com.capstone.pickIt.domain.course.entity.RecruitmentStatus;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitServiceImpl implements RecruitService {

    private final UserDefaultTraitRepository userDefaultTraitRepository;
    private final UserCourseProfileRepository userCourseProfileRepository;
    private final UserCourseTraitRepository userCourseTraitRepository;

    // 모집 탭 카드 목록 조회 (성향 유사순 정렬)
    @Override
    public List<RecruitProfileResponseDTO> getRecruitProfiles(Long userId, Long courseId, boolean includeCompleted) {

        // 1. 내 UserDefaultTrait 조회 -> Map<traitItemsId, selectedSide>
        List<UserDefaultTrait> myDefaultTraits = userDefaultTraitRepository.findByUserId(userId);
        Map<Long, TraitSide> myTraitMap = myDefaultTraits.stream()
                .collect(Collectors.toMap(
                        t -> t.getTraitItem().getTraitItemsId(),
                        UserDefaultTrait::getSelectedSide
                ));

        // 2. 조회할 상태 목록 결정
        // 미체크: RECRUITING + CONFIRM_PENDING
        // 체크:   RECRUITING + CONFIRM_PENDING + RECRUITMENT_COMPLETED
        List<RecruitmentStatus> statuses = new ArrayList<>(
                List.of(RecruitmentStatus.RECRUITING, RecruitmentStatus.CONFIRM_PENDING));
        if (includeCompleted) {
            statuses.add(RecruitmentStatus.RECRUITMENT_COMPLETED);
        }

        // 3. courseId로 해당 상태이고 내 카드 제외한 카드들 조회
        List<UserCourseProfile> profiles =
                userCourseProfileRepository.findProfilesExcludingUserByStatuses(courseId, userId, statuses);

        if (profiles.isEmpty()) {
            return List.of();
        }

        // 4. 해당 카드들의 성향 정보를 한 번에 조회 (N+1 방지)
        List<Long> profileIds = profiles.stream()
                .map(UserCourseProfile::getId)
                .toList();

        List<UserCourseTrait> allTraits =
                userCourseTraitRepository.findAllByUserCourseProfileIdIn(profileIds);

        // profileId → traits 매핑
        Map<Long, List<UserCourseTrait>> traitsByProfileId = allTraits.stream()
                .collect(Collectors.groupingBy(t -> t.getUserCourseProfile().getId()));

        // 5. 각 카드마다 traitScore 계산 후 DTO 변환, score 높은 순 정렬
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
