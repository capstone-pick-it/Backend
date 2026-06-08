package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.course.service.MatchScoreService;
import com.capstone.pickIt.api.user.dto.request.UserDefaultTraitRequestDTO;
import com.capstone.pickIt.api.user.dto.response.UserDefaultTraitResponseDTO;
import com.capstone.pickIt.domain.trait.entity.TraitItem;
import com.capstone.pickIt.domain.trait.entity.TraitSide;
import com.capstone.pickIt.domain.trait.exception.TraitErrorCode;
import com.capstone.pickIt.domain.trait.exception.TraitException;
import com.capstone.pickIt.domain.trait.repository.TraitItemRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.entity.UserDefaultTrait;
import com.capstone.pickIt.domain.user.exception.UserErrorCode;
import com.capstone.pickIt.domain.user.exception.UserException;
import com.capstone.pickIt.domain.user.repository.UserDefaultTraitRepository;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDefaultTraitService {

    private final UserDefaultTraitRepository userDefaultTraitRepository;
    private final UserRepository userRepository;
    private final TraitItemRepository traitItemRepository;
    private final MatchScoreService matchScoreService;

    // 기본 팀플 성향 조회
    @Transactional(readOnly = true)
    public List<UserDefaultTraitResponseDTO> getDefaultTraits(Long userId) {
        return userDefaultTraitRepository.findByUserId(userId)
                .stream()
                .map(UserDefaultTraitResponseDTO::from)
                .toList();
    }

    // 기본 팀플 성향 저장 (회원가입)
    @Transactional
    public void saveDefaultTraits(Long userId, List<UserDefaultTraitRequestDTO> requests) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 중복 검증
        List<Long> traitItemIds = requests.stream()
                .map(UserDefaultTraitRequestDTO::getTraitItemsId)
                .toList();

        if (traitItemIds.size() != traitItemIds.stream().distinct().count()) {
            throw new TraitException(TraitErrorCode.DUPLICATE_TRAIT_ITEM);
        }

        List<UserDefaultTrait> traits = requests.stream()
                .map(request -> {
                    TraitItem traitItem = traitItemRepository.findById(request.getTraitItemsId())
                            .orElseThrow(() -> new TraitException(TraitErrorCode.TRAIT_NOT_FOUND));
                    return UserDefaultTrait.builder()
                            .user(user)
                            .traitItem(traitItem)
                            .selectedSide(TraitSide.valueOf(request.getSelectedSide()))
                            .build();
                })
                .toList();

        userDefaultTraitRepository.saveAll(traits);
    }

    // 기본 팀플 성향 수정 (마이페이지)
    @Transactional
    public List<UserDefaultTraitResponseDTO> updateDefaultTraits(Long userId, List<UserDefaultTraitRequestDTO> requests) {
        userDefaultTraitRepository.deleteByUserId(userId);
        saveDefaultTraits(userId, requests);
        matchScoreService.recalculateForUser(userId);
        return getDefaultTraits(userId);
    }
}