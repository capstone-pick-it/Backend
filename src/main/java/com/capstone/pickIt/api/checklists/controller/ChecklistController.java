package com.capstone.pickIt.api.checklists.controller;

import com.capstone.pickIt.api.checklists.dto.request.ChecklistCreateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.request.ChecklistStatusUpdateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.request.ChecklistUpdateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.response.ChecklistItemResponseDTO;
import com.capstone.pickIt.api.checklists.dto.response.ChecklistListResponseDTO;
import com.capstone.pickIt.api.checklists.service.ChecklistService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService checklistService;

    @GetMapping("/projects/{projectTeamId}/checklists")
    public ApiResponse<ChecklistListResponseDTO> getChecklists(
            @PathVariable Long projectTeamId
    ) {
        ChecklistListResponseDTO response = checklistService.getChecklists(projectTeamId);
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @PostMapping("/projects/{projectTeamId}/checklists")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChecklistItemResponseDTO> createChecklist(
            @PathVariable Long projectTeamId,
            @Valid @RequestBody ChecklistCreateRequestDTO request
    ) {
        ChecklistItemResponseDTO response = checklistService.createChecklist(projectTeamId, request);
        return ApiResponse.onSuccess(SuccessCode.CREATED, response);
    }

    @PatchMapping("/checklists/{checklistItemId}")
    public ApiResponse<ChecklistItemResponseDTO> updateChecklist(
            @PathVariable Long checklistItemId,
            @Valid @RequestBody ChecklistUpdateRequestDTO request
    ) {
        ChecklistItemResponseDTO response = checklistService.updateChecklist(checklistItemId, request);
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @PatchMapping("/checklists/{checklistItemId}/status")
    public ApiResponse<ChecklistItemResponseDTO> updateChecklistStatus(
            @PathVariable Long checklistItemId,
            @Valid @RequestBody ChecklistStatusUpdateRequestDTO request
    ) {
        ChecklistItemResponseDTO response = checklistService.updateChecklistStatus(checklistItemId, request);
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @DeleteMapping("/checklists/{checklistItemId}")
    public ApiResponse<Void> deleteChecklist(
            @PathVariable Long checklistItemId
    ) {
        checklistService.deleteChecklist(checklistItemId);
        return ApiResponse.onSuccess(SuccessCode.NO_CONTENT, null);
    }
}