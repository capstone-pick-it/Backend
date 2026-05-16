package com.capstone.pickIt.api.checklists.controller;

import com.capstone.pickIt.api.checklists.dto.request.ChecklistCreateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.request.ChecklistStatusUpdateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.request.ChecklistUpdateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.response.ChecklistItemResponseDTO;
import com.capstone.pickIt.api.checklists.dto.response.ChecklistListResponseDTO;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChecklistController {

    @GetMapping("/projects/{projectTeamId}/checklists")
    public ApiResponse<ChecklistListResponseDTO> getChecklists(
            @PathVariable Long projectTeamId
    ) {
        // TODO: Service 연동
        ChecklistListResponseDTO response = null;

        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/projects/{projectTeamId}/checklists")
    public ApiResponse<ChecklistItemResponseDTO> createChecklist(
            @PathVariable Long projectTeamId,
            @Valid @RequestBody ChecklistCreateRequestDTO request
    ) {
        // TODO: Service 연동
        ChecklistItemResponseDTO response = null;

        return ApiResponse.onSuccess(SuccessCode.CREATED, response);
    }

    @PatchMapping("/checklists/{checklistItemId}")
    public ApiResponse<ChecklistItemResponseDTO> updateChecklist(
            @PathVariable Long checklistItemId,
            @Valid @RequestBody ChecklistUpdateRequestDTO request
    ) {
        // TODO: Service 연동
        ChecklistItemResponseDTO response = null;

        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @PatchMapping("/checklists/{checklistItemId}/status")
    public ApiResponse<ChecklistItemResponseDTO> updateChecklistStatus(
            @PathVariable Long checklistItemId,
            @Valid @RequestBody ChecklistStatusUpdateRequestDTO request
    ) {
        // TODO: Service 연동
        ChecklistItemResponseDTO response = null;

        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/checklists/{checklistItemId}")
    public ApiResponse<Void> deleteChecklist(
            @PathVariable Long checklistItemId
    ) {
        // TODO: Service 연동

        return ApiResponse.onSuccess(SuccessCode.NO_CONTENT, null);
    }
}