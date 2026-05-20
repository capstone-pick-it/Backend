package com.capstone.pickIt.api.checklists.service;

import com.capstone.pickIt.api.checklists.dto.request.ChecklistCreateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.request.ChecklistStatusUpdateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.request.ChecklistUpdateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.response.ChecklistItemResponseDTO;
import com.capstone.pickIt.api.checklists.dto.response.ChecklistListResponseDTO;

public interface ChecklistService {

    ChecklistListResponseDTO getChecklists(Long projectTeamId);

    ChecklistItemResponseDTO createChecklist(Long projectTeamId, ChecklistCreateRequestDTO request);

    ChecklistItemResponseDTO updateChecklist(Long checklistItemId, ChecklistUpdateRequestDTO request);

    ChecklistItemResponseDTO updateChecklistStatus(Long checklistItemId, ChecklistStatusUpdateRequestDTO request);

    void deleteChecklist(Long checklistItemId);
}