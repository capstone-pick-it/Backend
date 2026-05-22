package com.capstone.pickIt.api.user.dto.request;

import com.capstone.pickIt.domain.course.entity.ImportanceLevel;
import com.capstone.pickIt.domain.trait.entity.TraitSide;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "강의 추가 요청")
public class AddCourseRequestDTO {

    @NotBlank(message = "강의명은 필수입니다.")
    @Schema(description = "강의명", example = "캡스톤디자인")
    private String courseName;

    @NotBlank(message = "학기는 필수입니다.")
    @Schema(description = "학기", example = "2026-1")
    private String semester;

    @NotNull(message = "중요도는 필수입니다.")
    @Schema(description = "중요도 (HIGH, MEDIUM, LOW)", example = "HIGH")
    private ImportanceLevel importance;

    @NotEmpty(message = "성향 목록은 비어 있을 수 없습니다.")
    @Valid
    @Schema(description = "성향 선택 목록", example = "[{\"traitItemId\": 1, \"selectedType\": \"A\"}, {\"traitItemId\": 2, \"selectedType\": \"B\"}, {\"traitItemId\": 3, \"selectedType\": \"A\"}]")
    private List<TraitDTO> traits;

    @AssertTrue(message = "중복된 traitItemId는 허용되지 않습니다.")
    private boolean hasUniqueTraitItemIds() {
        if (traits == null) return true;
        return traits.stream()
                .map(TraitDTO::getTraitItemId)
                .distinct()
                .count() == traits.size();
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "성향 선택 항목")
    public static class TraitDTO {

        @NotNull(message = "성향 항목 ID는 필수입니다.")
        @Schema(description = "성향 항목 ID", example = "1")
        private Long traitItemId;

        @NotNull(message = "선택값은 필수입니다.")
        @Schema(description = "선택한 항목 (A 또는 B)", example = "A")
        private TraitSide selectedType;
    }
}