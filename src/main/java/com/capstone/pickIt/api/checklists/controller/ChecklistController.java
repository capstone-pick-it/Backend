package com.capstone.pickIt.api.checklists.controller;

import com.capstone.pickIt.api.checklists.dto.request.ChecklistCreateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.request.ChecklistStatusUpdateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.request.ChecklistUpdateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.response.ChecklistItemResponseDTO;
import com.capstone.pickIt.api.checklists.dto.response.ChecklistListResponseDTO;
import com.capstone.pickIt.api.checklists.service.ChecklistService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Checklist", description = "프로젝트 체크리스트 API")
@RestController
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService checklistService;

    @Operation(
            summary = "체크리스트 목록 조회",
            description = "프로젝트 팀의 체크리스트 목록을 조회합니다. " +
                    "미완료(TODO) 항목이 완료(DONE) 항목보다 먼저 정렬되며, " +
                    "마감일 오름차순, 생성일 오름차순 기준으로 정렬됩니다."
    )
    @GetMapping("/api/projects/{projectTeamId}/checklists")
    public ApiResponse<ChecklistListResponseDTO> getChecklists(
            @PathVariable Long projectTeamId
    ) {
        ChecklistListResponseDTO response = checklistService.getChecklists(projectTeamId);
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @Operation(
            summary = "체크리스트 생성",
            description = "프로젝트 팀에 새로운 체크리스트 항목을 생성합니다. " +
                    "담당자는 해당 프로젝트 팀의 활성 멤버여야 합니다."
    )
    @PostMapping("/api/projects/{projectTeamId}/checklists")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChecklistItemResponseDTO> createChecklist(
            @PathVariable Long projectTeamId,
            @Valid @RequestBody ChecklistCreateRequestDTO request
    ) {
        ChecklistItemResponseDTO response = checklistService.createChecklist(projectTeamId, request);
        return ApiResponse.onSuccess(SuccessCode.CREATED, response);
    }

    @Operation(
            summary = "체크리스트 수정",
            description = "체크리스트 제목 및 마감일을 수정합니다. " +
                    "체크리스트 담당자(manager)만 수정할 수 있으며, " +
                    "요청 값이 null인 필드는 기존 값이 유지됩니다."
    )
    @PatchMapping("/api/checklists/{checklistItemId}")
    public ApiResponse<ChecklistItemResponseDTO> updateChecklist(
            @PathVariable Long checklistItemId,
            @Valid @RequestBody ChecklistUpdateRequestDTO request
    ) {
        ChecklistItemResponseDTO response = checklistService.updateChecklist(checklistItemId, request);
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @Operation(
            summary = "체크리스트 완료 상태 변경",
            description = "체크리스트 완료 상태(TODO/DONE)를 변경합니다. " +
                    "체크리스트 담당자(manager)만 변경할 수 있습니다."
    )
    @PatchMapping("/api/checklists/{checklistItemId}/status")
    public ApiResponse<ChecklistItemResponseDTO> updateChecklistStatus(
            @PathVariable Long checklistItemId,
            @Valid @RequestBody ChecklistStatusUpdateRequestDTO request
    ) {
        ChecklistItemResponseDTO response = checklistService.updateChecklistStatus(checklistItemId, request);
        return ApiResponse.onSuccess(SuccessCode.OK, response);
    }

    @Operation(
            summary = "체크리스트 삭제",
            description = "체크리스트 항목을 삭제합니다. " +
                    "체크리스트 담당자(manager)만 삭제할 수 있으며, " +
                    "삭제 성공 시 204 No Content를 반환합니다."
    )
    @DeleteMapping("/api/checklists/{checklistItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChecklist(
            @PathVariable Long checklistItemId
    ) {
        checklistService.deleteChecklist(checklistItemId);
    }
}
