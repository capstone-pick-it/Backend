package com.capstone.pickIt.api.course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileStatusUpdateRequestDTO {

    @NotBlank(message = "모집 상태는 필수입니다.")
    @Pattern(regexp = "^(RECRUITING|CONFIRM_PENDING|RECRUITMENT_COMPLETED)$",
            message = "recruitmentStatus는 RECRUITING, CONFIRM_PENDING, RECRUITMENT_COMPLETED 중 하나여야 합니다.")
    private String recruitmentStatus;
}
