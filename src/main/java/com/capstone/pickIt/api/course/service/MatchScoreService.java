package com.capstone.pickIt.api.course.service;

import com.capstone.pickIt.domain.course.entity.ImportanceLevel;
import com.capstone.pickIt.domain.course.entity.RecruitmentStatus;
import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import com.capstone.pickIt.domain.course.entity.UserCourseTrait;
import com.capstone.pickIt.domain.course.repository.UserCourseProfileRepository;
import com.capstone.pickIt.domain.matching.entity.MatchScore;
import com.capstone.pickIt.domain.matching.repository.MatchScoreRepository;
import com.capstone.pickIt.domain.teamlevel.entity.TeamLevel;
import com.capstone.pickIt.domain.teamlevel.repository.TeamLevelRepository;
import com.capstone.pickIt.domain.trait.entity.TraitSide;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.entity.UserDefaultTrait;
import com.capstone.pickIt.domain.user.repository.UserDefaultTraitRepository;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchScoreService {

    private final MatchScoreRepository matchScoreRepository;
    private final UserCourseProfileRepository userCourseProfileRepository;
    private final UserDefaultTraitRepository userDefaultTraitRepository;
    private final TeamLevelRepository teamLevelRepository;
    private final UserRepository userRepository;

    // 카드 생성/수정 시 → 해당 카드에 대해 모든 유저의 점수 계산
    @Transactional
    public void recalculateForProfile(UserCourseProfile targetProfile) {
        // 기존 점수 삭제
        matchScoreRepository.deleteByProfileId(targetProfile.getId());

        // 해당 과목의 다른 유저 목록 조회
        List<UserCourseProfile> otherProfiles = userCourseProfileRepository
                .findAllByUserIdAndDeletedAtIsNull(targetProfile.getUser().getId());

        // 같은 과목의 모든 유저 조회 (본인 제외)
        List<User> allUsers = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(targetProfile.getUser().getId()))
                .toList();

        // 각 유저와의 점수 계산
        List<MatchScore> scores = allUsers.stream()
                .map(user -> calculateScore(user, targetProfile))
                .toList();

        matchScoreRepository.saveAll(scores);
    }

    // 기본 팀플 성향 수정 시 → 해당 유저가 조회하는 모든 점수 재계산
    @Transactional
    public void recalculateForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        // 기존 점수 삭제
        matchScoreRepository.deleteByUserId(userId);

        // 활성화된 모든 카드 조회 (본인 카드 제외)
        List<UserCourseProfile> allProfiles = userCourseProfileRepository.findAll().stream()
                .filter(p -> !p.getUser().getId().equals(userId))
                .filter(p -> p.getDeletedAt() == null)
                .filter(p -> p.getRecruitmentStatus() != RecruitmentStatus.RECRUITMENT_COMPLETED)
                .toList();

        List<MatchScore> scores = allProfiles.stream()
                .map(profile -> calculateScore(user, profile))
                .toList();

        matchScoreRepository.saveAll(scores);
    }

    private MatchScore calculateScore(User user, UserCourseProfile targetProfile) {
        int traitScore = calculateTraitScore(user.getId(), targetProfile);
        int importanceScore = calculateImportanceScore(user.getId(), targetProfile);
        int levelScore = calculateLevelScore(user.getId(), targetProfile.getUser().getId());

        return MatchScore.of(user, targetProfile, traitScore, importanceScore, levelScore);
    }

    // 성향 일치도 (최대 5점)
    private int calculateTraitScore(Long userId, UserCourseProfile targetProfile) {
        List<UserDefaultTrait> myTraits = userDefaultTraitRepository.findByUserId(userId);
        List<UserCourseTrait> targetTraits = targetProfile.getTraits();

        Map<Long, TraitSide> myTraitMap = myTraits.stream()
                .collect(Collectors.toMap(
                        t -> t.getTraitItem().getTraitItemsId(),
                        UserDefaultTrait::getSelectedSide
                ));

        int score = 0;
        for (UserCourseTrait targetTrait : targetTraits) {
            Long traitItemId = targetTrait.getTraitItem().getTraitItemsId();
            TraitSide mySide = myTraitMap.get(traitItemId);
            if (mySide != null && mySide == targetTrait.getSelectedSide()) {
                score++;
            }
        }
        return score;
    }

    // 중요도 일치도 (최대 3점)
    private int calculateImportanceScore(Long userId, UserCourseProfile targetProfile) {
        // 내 카드 중 같은 과목 카드 조회
        UserCourseProfile myProfile = userCourseProfileRepository
                .findByUserIdAndCourseIdAndDeletedAtIsNull(userId, targetProfile.getCourse().getId())
                .orElse(null);

        if (myProfile == null) return 0;

        int myLevel = importanceToInt(myProfile.getImportanceLevel());
        int targetLevel = importanceToInt(targetProfile.getImportanceLevel());
        int diff = Math.abs(myLevel - targetLevel);

        return Math.max(3 - diff, 0);
    }

    // 팀플 레벨 (최대 3점)
    private int calculateLevelScore(Long myUserId, Long targetUserId) {
        Optional<TeamLevel> myTeamLevel = teamLevelRepository.findByUserId(myUserId);
        Optional<TeamLevel> targetTeamLevel = teamLevelRepository.findByUserId(targetUserId);

        // 한쪽이라도 레벨 정보 없으면 0점
        if (myTeamLevel.isEmpty() || targetTeamLevel.isEmpty()) {
            return 0;
        }

        int myLevel = myTeamLevel.get().getLevel();
        int targetLevel = targetTeamLevel.get().getLevel();
        int diff = Math.abs(myLevel - targetLevel);
        return Math.max(3 - diff, 0);
    }

    private int importanceToInt(ImportanceLevel level) {
        return switch (level) {
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }
}