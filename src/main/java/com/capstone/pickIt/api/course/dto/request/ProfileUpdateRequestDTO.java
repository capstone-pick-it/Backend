package com.capstone.pickIt.api.course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequestDTO {

    @NotBlank(message = "중요도는 필수입니다.")
    @Pattern(regexp = "^(HIGH|MEDIUM|LOW)$", message = "importanceLevel은 HIGH, MEDIUM, LOW 중 하나여야 합니다.")
    private String importanceLevel;
}
