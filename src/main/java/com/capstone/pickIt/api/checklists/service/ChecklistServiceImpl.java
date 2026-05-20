package com.capstone.pickIt.api.checklists.service;

import com.capstone.pickIt.api.checklists.converter.ChecklistConverter;
import com.capstone.pickIt.api.checklists.dto.request.ChecklistCreateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.request.ChecklistStatusUpdateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.request.ChecklistUpdateRequestDTO;
import com.capstone.pickIt.api.checklists.dto.response.ChecklistItemResponseDTO;
import com.capstone.pickIt.api.checklists.dto.response.ChecklistListResponseDTO;
import com.capstone.pickIt.domain.project.entity.ChecklistItem;
import com.capstone.pickIt.domain.project.entity.ChecklistStatus;
import com.capstone.pickIt.domain.project.entity.ProjectTeam;
import com.capstone.pickIt.domain.project.entity.RecruitmentConfirmStatus;
import com.capstone.pickIt.domain.project.exception.ChecklistErrorCode;
import com.capstone.pickIt.domain.project.exception.ChecklistException;
import com.capstone.pickIt.domain.project.repository.ChecklistItemRepository;
import com.capstone.pickIt.domain.project.repository.ProjectTeamMemberRepository;
import com.capstone.pickIt.domain.project.repository.ProjectTeamRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChecklistServiceImpl implements ChecklistService {

    private final ChecklistItemRepository checklistItemRepository;
    private final ProjectTeamRepository projectTeamRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final UserRepository userRepository;

    @Override
    public ChecklistListResponseDTO getChecklists(Long projectTeamId) {
        Long currentUserId = SecurityUtil.requireUserId();

        ProjectTeam projectTeam = findProjectTeam(projectTeamId);
        validateActiveProjectMember(projectTeamId, currentUserId);

        List<ChecklistItemResponseDTO> checklists = checklistItemRepository
                .findActiveItemsOrderByTodoFirstDueDateAscCreatedAtAsc(projectTeam)
                .stream()
                .map(ChecklistConverter::toResponse)
                .toList();

        return new ChecklistListResponseDTO(projectTeamId, checklists);
    }

    @Override
    @Transactional
    public ChecklistItemResponseDTO createChecklist(Long projectTeamId, ChecklistCreateRequestDTO request) {
        Long currentUserId = SecurityUtil.requireUserId();

        ProjectTeam projectTeam = findProjectTeam(projectTeamId);
        validateActiveProjectMember(projectTeamId, currentUserId);
        validateManagerIsActiveProjectMember(projectTeamId, request.managerId());

        User manager = findUser(request.managerId());
        User createdByUser = findUser(currentUserId);

        ChecklistItem checklistItem = ChecklistItem.builder()
                .projectTeam(projectTeam)
                .manager(manager)
                .title(request.title())
                .dueDate(request.dueDate())
                .createdByUser(createdByUser)
                .build();

        ChecklistItem savedChecklistItem = checklistItemRepository.save(checklistItem);

        return ChecklistConverter.toResponse(savedChecklistItem);
    }

    @Override
    @Transactional
    public ChecklistItemResponseDTO updateChecklist(Long checklistItemId, ChecklistUpdateRequestDTO request) {
        Long currentUserId = SecurityUtil.requireUserId();

        validateUpdateRequest(request);

        ChecklistItem checklistItem = findChecklistItem(checklistItemId);
        Long projectTeamId = checklistItem.getProjectTeam().getId();

        validateActiveProjectMember(projectTeamId, currentUserId);
        validateChecklistManager(checklistItem, currentUserId);

        checklistItem.updateContent(request.title(), request.dueDate());

        return ChecklistConverter.toResponse(checklistItem);
    }

    @Override
    @Transactional
    public ChecklistItemResponseDTO updateChecklistStatus(
            Long checklistItemId,
            ChecklistStatusUpdateRequestDTO request
    ) {
        Long currentUserId = SecurityUtil.requireUserId();

        ChecklistItem checklistItem = findChecklistItem(checklistItemId);
        Long projectTeamId = checklistItem.getProjectTeam().getId();

        validateActiveProjectMember(projectTeamId, currentUserId);
        validateChecklistManager(checklistItem, currentUserId);

        User completedByUser = findUser(currentUserId);

        if (request.status() == null) {
            throw new ChecklistException(ChecklistErrorCode.INVALID_UPDATE_REQUEST);
        } else if (request.status() == ChecklistStatus.DONE) {
            checklistItem.markDone(completedByUser);
        } else if (request.status() == ChecklistStatus.TODO) {
            checklistItem.markTodo();
        } else {
            throw new ChecklistException(ChecklistErrorCode.INVALID_UPDATE_REQUEST);
        }

        return ChecklistConverter.toResponse(checklistItem);
    }

    @Override
    @Transactional
    public void deleteChecklist(Long checklistItemId) {
        Long currentUserId = SecurityUtil.requireUserId();

        ChecklistItem checklistItem = findChecklistItem(checklistItemId);
        Long projectTeamId = checklistItem.getProjectTeam().getId();

        validateActiveProjectMember(projectTeamId, currentUserId);
        validateChecklistManager(checklistItem, currentUserId);

        checklistItem.softDelete();
    }

    private ProjectTeam findProjectTeam(Long projectTeamId) {
        return projectTeamRepository.findById(projectTeamId)
                .orElseThrow(() -> new ChecklistException(ChecklistErrorCode.PROJECT_TEAM_NOT_FOUND));
    }

    private ChecklistItem findChecklistItem(Long checklistItemId) {
        return checklistItemRepository.findByIdAndDeletedAtIsNull(checklistItemId)
                .orElseThrow(() -> new ChecklistException(ChecklistErrorCode.CHECKLIST_NOT_FOUND));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ChecklistException(ChecklistErrorCode.USER_NOT_FOUND));
    }

    private void validateUpdateRequest(ChecklistUpdateRequestDTO request) {
        if (request.title() == null && request.dueDate() == null) {
            throw new ChecklistException(ChecklistErrorCode.INVALID_UPDATE_REQUEST);
        }

        if (request.title() != null && request.title().isBlank()) {
            throw new ChecklistException(ChecklistErrorCode.INVALID_TITLE);
        }
    }

    private void validateActiveProjectMember(Long projectTeamId, Long userId) {
        boolean exists = projectTeamMemberRepository.existsActiveConfirmedMember(
                projectTeamId,
                userId,
                RecruitmentConfirmStatus.CONFIRMED
        );

        if (!exists) {
            throw new ChecklistException(ChecklistErrorCode.NOT_PROJECT_MEMBER);
        }
    }

    private void validateChecklistManager(ChecklistItem checklistItem, Long userId) {
        if (!checklistItem.getManager().getId().equals(userId)) {
            throw new ChecklistException(ChecklistErrorCode.ONLY_MANAGER_CAN_UPDATE_CHECKLIST);
        }
    }

    private void validateManagerIsActiveProjectMember(Long projectTeamId, Long managerId) {
        boolean exists = projectTeamMemberRepository.existsActiveConfirmedMember(
                projectTeamId,
                managerId,
                RecruitmentConfirmStatus.CONFIRMED
        );

        if (!exists) {
            throw new ChecklistException(ChecklistErrorCode.MANAGER_NOT_PROJECT_MEMBER);
        }
    }
}