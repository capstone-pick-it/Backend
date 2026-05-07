package com.capstone.pickIt.api.checklists.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ChecklistCreateRequestDTO(
        @NotBlank
        @Size(max = 200)
        String title,

        LocalDate dueDate,

        @NotNull
        Long managerId
) {
}