package com.capstone.pickIt.api.checklists.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ChecklistUpdateRequestDTO(
        @Size(max = 200)
        String title,

        LocalDate dueDate
) {
}