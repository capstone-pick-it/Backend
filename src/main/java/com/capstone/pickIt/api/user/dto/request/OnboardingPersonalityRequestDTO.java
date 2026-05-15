package com.capstone.pickIt.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
                    { "traitItemId": 4, "selectedType": "A" }
                  ]
                }
                """
)
public class OnboardingPersonalityRequestDTO {

    @NotEmpty(message = "성향 목록은 비어 있을 수 없습니다.")
    @Valid
    @Schema(description = "팀플 성향 선택 목록 (전체 스탭 선택값을 한 번에 담아서 전송)")
    private List<TraitItem> traits;

    @Getter
    @NoArgsConstructor
    @Schema(description = "개별 성향 선택 항목")
    public static class TraitItem {

        @NotNull(message = "성향 항목 ID는 필수입니다.")
        @Schema(description = "성향 항목 ID", example = "1")
        private Long traitItemId;

        @NotNull(message = "선택값은 필수입니다.")
        @Pattern(regexp = "^(A|B)$", message = "selectedType은 A 또는 B만 가능합니다.")
        @Schema(description = "선택한 항목 (A 또는 B). nameA 선택 시 A, nameB 선택 시 B", example = "A", allowableValues = {"A", "B"})
        private String selectedType;
    }
}