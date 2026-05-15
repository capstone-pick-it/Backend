package com.capstone.pickIt.api.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDefaultTraitRequestDTO {

    @NotNull(message = "성향 항목 ID는 필수입니다.")
    private Long traitItemsId;

    @NotBlank(message = "선택값은 필수입니다.")
    private String selectedSide;
}