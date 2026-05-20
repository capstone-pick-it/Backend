package com.capstone.pickIt.api.checklists.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ChecklistUpdateRequestDTO(
        @Size(max = 200)
        String title,

        LocalDate dueDate
) {
    public ChecklistUpdateRequestDTO {
        if (title == null && dueDate == null) {
            throw new IllegalArgumentException("title 또는 dueDate 중 하나는 반드시 입력해야 합니다.");
        }
    }
}