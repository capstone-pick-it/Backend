package com.capstone.pickIt.api.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDefaultTraitRequestDTO {

    private Long traitItemsId;
    private String selectedSide;
}