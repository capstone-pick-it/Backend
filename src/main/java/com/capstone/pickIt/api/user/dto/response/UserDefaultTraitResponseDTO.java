package com.capstone.pickIt.api.user.dto.response;

import com.capstone.pickIt.domain.user.entity.UserDefaultTrait;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDefaultTraitResponseDTO {

    private Long defaultTraitId;
    private Long traitItemsId;
    private String nameA;
    private String nameB;
    private String selectedSide;

    public static UserDefaultTraitResponseDTO from(UserDefaultTrait userDefaultTrait) {
        return UserDefaultTraitResponseDTO.builder()
                .defaultTraitId(userDefaultTrait.getDefaultTraitId())
                .traitItemsId(userDefaultTrait.getTraitItem().getTraitItemsId())
                .nameA(userDefaultTrait.getTraitItem().getNameA())
                .nameB(userDefaultTrait.getTraitItem().getNameB())
                .selectedSide(userDefaultTrait.getSelectedSide().name())
                .build();
    }
}