package com.capstone.pickIt.api.user.dto.request;

import com.capstone.pickIt.domain.trait.entity.TraitSide;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(
        description = "온보딩 팀플 성향 저장 요청. 모든 스탭 선택 완료 후 한 번에 전송해야 합니다.",
        example = """
                {
                  "traits": [
                    { "traitItemId": 1, "selectedType": "A" },
                    { "traitItemId": 2, "selectedType": "B" },
                    { "traitItemId": 3, "selectedType": "A" },
                    { "traitItemId": 4, "selectedType": "A" },
                    { "traitItemId": 5, "selectedType": "B" }
                  ]
                }
                """
)
public class OnboardingPersonalityRequestDTO {

    private static final int TRAIT_COUNT = 5;

    @NotEmpty(message = "성향 목록은 비어 있을 수 없습니다.")
    @Size(min = TRAIT_COUNT, max = TRAIT_COUNT, message = "성향 목록은 5개 항목을 모두 포함해야 합니다.")
    @Valid
    @Schema(description = "팀플 성향 선택 목록 (전체 스탭 선택값을 한 번에 담아서 전송)")
    private List<TraitItem> traits;

    @AssertTrue(message = "중복된 traitItemId는 허용되지 않습니다.")
    private boolean hasUniqueTraitItemIds() {
        if (traits == null) return true;
        return traits.stream()
                .map(TraitItem::getTraitItemId)
                .distinct()
                .count() == traits.size();
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "개별 성향 선택 항목")
    public static class TraitItem {

        @NotNull(message = "성향 항목 ID는 필수입니다.")
        @Schema(description = "성향 항목 ID", example = "1")
        private Long traitItemId;

        @NotNull(message = "선택값은 필수입니다.")
        @Schema(description = "선택한 항목 (A 또는 B). nameA 선택 시 A, nameB 선택 시 B", example = "A")
        private TraitSide selectedType;
    }
}