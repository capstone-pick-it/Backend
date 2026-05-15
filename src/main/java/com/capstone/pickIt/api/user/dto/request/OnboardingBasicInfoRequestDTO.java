package com.capstone.pickIt.api.user.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OnboardingBasicInfoRequestDTO {

    @NotBlank
    private String school;

    @NotBlank
    private String major;

    @NotNull
    @Min(1) @Max(6)
    private Integer grade;

    @NotBlank
    private String semester;

    @NotNull
    private List<String> courses;
}