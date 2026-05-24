package com.capstone.pickIt.api.project.service;

import com.capstone.pickIt.api.project.converter.ProjectCompletionConverter;
import com.capstone.pickIt.api.project.dto.request.CompletionDecisionRequestDTO;
import com.capstone.pickIt.api.project.dto.response.CompletionRequestResponseDTO;
import com.capstone.pickIt.api.teamlevel.service.TeamLevelService;
import com.capstone.pickIt.domain.project.entity.*;
import com.capstone.pickIt.domain.project.exception.ProjectCompletionErrorCode;
import com.capstone.pickIt.domain.project.exception.ProjectCompletionException;
import com.capstone.pickIt.domain.project.repository.*;
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
public class ProjectCompletionServiceImpl implements ProjectCompletionService {

    private final ProjectTeamRepository projectTeamRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final ProjectTeamCompletionRequestRepository completionRequestRepository;
    private final ProjectTeamCompletionApprovalRepository completionApprovalRepository;
    private final UserRepository userRepository;
    private final TeamLevelService teamLevelService;

    @Override
    @Transactional
    public CompletionRequestResponseDTO createCompletionRequest(Long projectTeamId) {
        Long currentUserId = SecurityUtil.requireUserId();

        ProjectTeam projectTeam = findProjectTeam(projectTeamId);
        validateActiveProjectMember(projectTeamId, currentUserId);

        if (projectTeam.getStatus() != ProjectTeamStatus.IN_PROGRESS) {
            throw new ProjectCompletionException(ProjectCompletionErrorCode.PROJECT_NOT_IN_PROGRESS);
        }

        if (completionRequestRepository.existsByProjectTeamIdAndStatus(
                projectTeamId,
                CompletionRequestStatus.PENDING
        )) {
            throw new ProjectCompletionException(ProjectCompletionErrorCode.ALREADY_PENDING_COMPLETION_REQUEST);
        }

        ProjectTeamCompletionRequest completionRequest = ProjectTeamCompletionRequest.builder()
                .projectTeam(projectTeam)
                .requester(findUser(currentUserId))
                .status(CompletionRequestStatus.PENDING)
                .build();

        ProjectTeamCompletionRequest saved = completionRequestRepository.save(completionRequest);

        return toResponse(saved);
    }

    @Override
    public CompletionRequestResponseDTO getCurrentCompletionRequest(Long projectTeamId) {
        Long currentUserId = SecurityUtil.requireUserId();

        findProjectTeam(projectTeamId);
        validateActiveProjectMember(projectTeamId, currentUserId);

        ProjectTeamCompletionRequest request = completionRequestRepository
                .findTopByProjectTeamIdAndStatusOrderByRequestedAtDesc(
                        projectTeamId,
                        CompletionRequestStatus.PENDING
                )
                .orElseThrow(() -> new ProjectCompletionException(
                        ProjectCompletionErrorCode.COMPLETION_REQUEST_NOT_FOUND
                ));

        return toResponse(request);
    }

    @Override
    @Transactional
    public CompletionRequestResponseDTO decideCompletionRequest(
            Long completionRequestId,
            CompletionDecisionRequestDTO request
    ) {
        Long currentUserId = SecurityUtil.requireUserId();

        ProjectTeamCompletionRequest completionRequest = completionRequestRepository
                .findById(completionRequestId)
                .orElseThrow(() -> new ProjectCompletionException(
                        ProjectCompletionErrorCode.COMPLETION_REQUEST_NOT_FOUND
                ));

        Long projectTeamId = completionRequest.getProjectTeam().getId();

        validateActiveProjectMember(projectTeamId, currentUserId);

        if (completionRequest.getStatus() != CompletionRequestStatus.PENDING) {
            throw new ProjectCompletionException(
                    ProjectCompletionErrorCode.COMPLETION_REQUEST_ALREADY_FINALIZED
            );
        }

        if (completionRequest.getRequester().getId().equals(currentUserId)) {
            throw new ProjectCompletionException(ProjectCompletionErrorCode.REQUESTER_CANNOT_DECIDE);
        }

        ProjectTeamCompletionApproval approval = completionApprovalRepository
                .findByCompletionRequestIdAndUserId(completionRequestId, currentUserId)
                .orElseGet(() -> ProjectTeamCompletionApproval.builder()
                        .completionRequest(completionRequest)
                        .user(findUser(currentUserId))
                        .decision(request.decision())
                        .build()
                );

        approval.changeDecision(request.decision());
        completionApprovalRepository.save(approval);

        if (request.decision() == CompletionDecision.REJECT) {
            completionRequest.reject();
            return toResponse(completionRequest);
        }

        finalizeIfAllMembersApproved(completionRequest);

        return toResponse(completionRequest);
    }

    private void finalizeIfAllMembersApproved(ProjectTeamCompletionRequest completionRequest) {
        Long projectTeamId = completionRequest.getProjectTeam().getId();

        List<ProjectTeamMember> members = projectTeamMemberRepository.findByProjectTeam_Id(projectTeamId)
                .stream()
                .filter(ProjectTeamMember::isActiveMember)
                .toList();

        long requiredApprovalCount = members.stream()
                .filter(member -> !member.getUser().getId().equals(completionRequest.getRequester().getId()))
                .count();

        long approvedCount = completionApprovalRepository
                .countByCompletionRequestIdAndDecision(
                        completionRequest.getId(),
                        CompletionDecision.APPROVE
                );

        if (approvedCount >= requiredApprovalCount) {
            completionRequest.approve();
            completionRequest.getProjectTeam().complete();

            members.forEach(member -> teamLevelService.recalculate(member.getUser().getId()));
        }
    }

    private CompletionRequestResponseDTO toResponse(ProjectTeamCompletionRequest request) {
        List<ProjectTeamCompletionApproval> approvals =
                completionApprovalRepository.findAllByCompletionRequestId(request.getId());

        return ProjectCompletionConverter.toResponse(request, approvals);
    }

    private ProjectTeam findProjectTeam(Long projectTeamId) {
        return projectTeamRepository.findById(projectTeamId)
                .orElseThrow(() -> new ProjectCompletionException(
                        ProjectCompletionErrorCode.PROJECT_TEAM_NOT_FOUND
                ));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ProjectCompletionException(
                        ProjectCompletionErrorCode.USER_NOT_FOUND
                ));
    }

    private void validateActiveProjectMember(Long projectTeamId, Long userId) {
        boolean exists = projectTeamMemberRepository.existsActiveConfirmedMember(
                projectTeamId,
                userId,
                RecruitmentConfirmStatus.CONFIRMED
        );

        if (!exists) {
            throw new ProjectCompletionException(ProjectCompletionErrorCode.NOT_PROJECT_MEMBER);
        }
    }
}
