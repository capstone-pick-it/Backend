package com.capstone.pickIt.api.project.service;

import com.capstone.pickIt.api.project.converter.PeerReviewConverter;
import com.capstone.pickIt.api.project.dto.request.PeerReviewCreateRequestDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewResponseDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewStatusResponseDTO;
import com.capstone.pickIt.api.project.dto.response.PeerReviewTargetListResponseDTO;
import com.capstone.pickIt.domain.project.entity.PeerReview;
import com.capstone.pickIt.domain.project.entity.ProjectTeam;
import com.capstone.pickIt.domain.project.entity.ProjectTeamMember;
import com.capstone.pickIt.domain.project.entity.ProjectTeamReviewStatus;
import com.capstone.pickIt.domain.project.entity.ProjectTeamStatus;
import com.capstone.pickIt.domain.project.entity.RecruitmentConfirmStatus;
import com.capstone.pickIt.domain.project.exception.PeerReviewErrorCode;
import com.capstone.pickIt.domain.project.exception.PeerReviewException;
import com.capstone.pickIt.domain.project.repository.PeerReviewRepository;
import com.capstone.pickIt.domain.project.repository.ProjectTeamMemberRepository;
import com.capstone.pickIt.domain.project.repository.ProjectTeamRepository;
import com.capstone.pickIt.domain.project.repository.ProjectTeamReviewStatusRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PeerReviewServiceImpl implements PeerReviewService {

    private final ProjectTeamRepository projectTeamRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final PeerReviewRepository peerReviewRepository;
    private final ProjectTeamReviewStatusRepository projectTeamReviewStatusRepository;
    private final UserRepository userRepository;

    @Override
    public PeerReviewTargetListResponseDTO getPeerReviewTargets(Long projectTeamId) {
        Long currentUserId = SecurityUtil.requireUserId();

        ProjectTeam projectTeam = findProjectTeam(projectTeamId);
        validateProjectCompleted(projectTeam);
        validateActiveProjectMember(projectTeamId, currentUserId);

        List<ProjectTeamMember> members = projectTeamMemberRepository
                .findActiveConfirmedMembersWithUser(
                        projectTeamId,
                        RecruitmentConfirmStatus.CONFIRMED
                )
                .stream()
                .filter(member -> !member.getUser().getId().equals(currentUserId))
                .toList();

        Set<Long> reviewedUserIds = peerReviewRepository
                .findAllByProjectTeamIdAndReviewerId(projectTeamId, currentUserId)
                .stream()
                .map(review -> review.getReviewee().getId())
                .collect(Collectors.toSet());

        return PeerReviewConverter.toTargetListResponse(
                projectTeamId,
                members,
                reviewedUserIds
        );
    }

    @Override
    @Transactional
    public PeerReviewResponseDTO createPeerReview(
            Long projectTeamId,
            PeerReviewCreateRequestDTO request
    ) {
        Long currentUserId = SecurityUtil.requireUserId();

        ProjectTeam projectTeam = findProjectTeam(projectTeamId);
        validateProjectCompleted(projectTeam);
        validateActiveProjectMember(projectTeamId, currentUserId);
        validateActiveProjectMember(projectTeamId, request.revieweeUserId());

        if (currentUserId.equals(request.revieweeUserId())) {
            throw new PeerReviewException(PeerReviewErrorCode.CANNOT_REVIEW_SELF);
        }

        if (peerReviewRepository.existsByProjectTeamIdAndReviewerIdAndRevieweeId(
                projectTeamId,
                currentUserId,
                request.revieweeUserId()
        )) {
            throw new PeerReviewException(PeerReviewErrorCode.ALREADY_REVIEWED);
        }

        PeerReview peerReview = PeerReview.builder()
                .projectTeam(projectTeam)
                .reviewer(findUser(currentUserId))
                .reviewee(findUser(request.revieweeUserId()))
                .completionScore(request.completionScore())
                .proactivityScore(request.proactivityScore())
                .satisfactionScore(request.satisfactionScore())
                .build();

        PeerReview saved = peerReviewRepository.save(peerReview);

        projectTeamReviewStatusRepository
                .findByProjectTeamIdAndUserId(projectTeamId, currentUserId)
                .ifPresent(ProjectTeamReviewStatus::increaseSubmittedCount);

        return PeerReviewConverter.toResponse(saved);
    }

    @Override
    public PeerReviewStatusResponseDTO getPeerReviewStatus(Long projectTeamId) {
        Long currentUserId = SecurityUtil.requireUserId();

        ProjectTeam projectTeam = findProjectTeam(projectTeamId);
        validateProjectCompleted(projectTeam);
        validateActiveProjectMember(projectTeamId, currentUserId);

        List<ProjectTeamMember> targets = projectTeamMemberRepository
                .findActiveConfirmedMembersWithUser(
                        projectTeamId,
                        RecruitmentConfirmStatus.CONFIRMED
                )
                .stream()
                .filter(member -> !member.getUser().getId().equals(currentUserId))
                .toList();

        Set<Long> submittedRevieweeIds = peerReviewRepository
                .findAllByProjectTeamIdAndReviewerId(projectTeamId, currentUserId)
                .stream()
                .map(review -> review.getReviewee().getId())
                .collect(Collectors.toSet());

        int requiredCount = targets.size();
        int submittedCount = submittedRevieweeIds.size();
        boolean isCompleted = submittedCount >= requiredCount;

        return PeerReviewConverter.toStatusResponse(
                projectTeamId,
                requiredCount,
                submittedCount,
                isCompleted,
                targets,
                submittedRevieweeIds
        );
    }

    private ProjectTeam findProjectTeam(Long projectTeamId) {
        return projectTeamRepository.findById(projectTeamId)
                .orElseThrow(() -> new PeerReviewException(PeerReviewErrorCode.PROJECT_TEAM_NOT_FOUND));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PeerReviewException(PeerReviewErrorCode.USER_NOT_FOUND));
    }

    private void validateProjectCompleted(ProjectTeam projectTeam) {
        if (projectTeam.getStatus() != ProjectTeamStatus.DONE) {
            throw new PeerReviewException(PeerReviewErrorCode.PROJECT_NOT_COMPLETED);
        }
    }

    private void validateActiveProjectMember(Long projectTeamId, Long userId) {
        boolean exists = projectTeamMemberRepository.existsActiveConfirmedMember(
                projectTeamId,
                userId,
                RecruitmentConfirmStatus.CONFIRMED
        );

        if (!exists) {
            throw new PeerReviewException(PeerReviewErrorCode.NOT_PROJECT_MEMBER);
        }
    }
}