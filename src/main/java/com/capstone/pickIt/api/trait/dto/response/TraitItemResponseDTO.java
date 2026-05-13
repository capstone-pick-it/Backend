package com.capstone.pickIt.api.trait.dto.response;

import com.capstone.pickIt.domain.trait.entity.TraitItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TraitItemResponseDTO {

    private Long traitItemsId;
    private String nameA;
    private String nameB;
    private boolean complementaryAllowed;

    public static TraitItemResponseDTO from(TraitItem traitItem) {
        return TraitItemResponseDTO.builder()
                .traitItemsId(traitItem.getTraitItemsId())
                .nameA(traitItem.getNameA())
                .nameB(traitItem.getNameB())
                .complementaryAllowed(traitItem.isComplementaryAllowed())
                .build();
    }
}