package com.capstone.pickIt.api.course.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCreateRequestDTO {

    @NotNull(message = "강의 ID는 필수입니다.")
    private Long courseId;

    @Pattern(regexp = "^(HIGH|MEDIUM|LOW)$", message = "importanceLevel은 HIGH, MEDIUM, LOW 중 하나여야 합니다.")
    private String importanceLevel;
}
