package com.capstone.pickIt.api.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDefaultTraitRequestDTO {

    @NotNull(message = "성향 항목 ID는 필수입니다.")
    private Long traitItemsId;

    @NotBlank(message = "선택값은 필수입니다.")
    @Pattern(regexp = "^(A|B)$", message = "selectedSide는 A 또는 B만 가능합니다.")
    private String selectedSide;
}