package com.capstone.pickIt.api.course.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequestDTO {

    @Pattern(regexp = "^(HIGH|MEDIUM|LOW)$", message = "importanceLevel은 HIGH, MEDIUM, LOW 중 하나여야 합니다.")
    private String importanceLevel;

    @Valid
    private List<TraitUpdateDTO> traits;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TraitUpdateDTO {

        @NotNull(message = "성향 항목 ID는 필수입니다.")
        private Long traitItemsId;

        @NotBlank(message = "선택값은 필수입니다.")
        @Pattern(regexp = "^(A|B)$", message = "selectedSide는 A 또는 B만 가능합니다.")
        private String selectedSide;
    }
}
