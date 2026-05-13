package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.request.UserDefaultTraitRequestDTO;
import com.capstone.pickIt.api.user.dto.response.UserDefaultTraitResponseDTO;
import com.capstone.pickIt.domain.trait.entity.TraitItem;
import com.capstone.pickIt.domain.trait.entity.TraitSide;
import com.capstone.pickIt.domain.trait.repository.TraitItemRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.entity.UserDefaultTrait;
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        List<UserDefaultTrait> traits = requests.stream()
                .map(request -> {
                    TraitItem traitItem = traitItemRepository.findById(request.getTraitItemsId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 성향 항목입니다."));
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
    public void updateDefaultTraits(Long userId, List<UserDefaultTraitRequestDTO> requests) {
        userDefaultTraitRepository.deleteByUserId(userId);
        saveDefaultTraits(userId, requests);
    }
}