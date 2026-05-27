package com.capstone.pickIt.api.project.service;

import com.capstone.pickIt.api.chat.service.ChatRoomCommandService;
import com.capstone.pickIt.api.project.dto.response.ConfirmResponseDTO;
import com.capstone.pickIt.api.project.dto.response.TeamLeaveRequestResponseDTO;
import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import com.capstone.pickIt.domain.course.entity.RecruitmentStatus;
import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import com.capstone.pickIt.domain.course.repository.UserCourseProfileRepository;
import com.capstone.pickIt.domain.project.entity.*;
import com.capstone.pickIt.domain.project.exception.ProjectErrorCode;
import com.capstone.pickIt.domain.project.exception.ProjectException;
import com.capstone.pickIt.domain.project.repository.*;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.exception.UserErrorCode;
import com.capstone.pickIt.domain.user.exception.UserException;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamMemberServiceImpl implements TeamMemberService {

    private final ProjectTeamRepository projectTeamRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final TeamLeaveRequestRepository teamLeaveRequestRepository;
    private final TeamLeaveApprovalRepository teamLeaveApprovalRepository;
    private final UserCourseProfileRepository userCourseProfileRepository;
    private final UserRepository userRepository;
    private final ChatRoomCommandService chatRoomCommandService;


     // 팀원 확정
     // PENDING -> CONFIRMED
     // first_confirmed_at 기록 (최초 1회)
     // 48시간 초과 시 예외
     // 전원 확정 시 -> ProjectTeam IN_PROGRESS, 모든 카드 RECRUITMENT_COMPLETED
    @Override
    @Transactional
    public ConfirmResponseDTO confirm(Long projectTeamId) {
        Long userId = SecurityUtil.requireUserId();

        ProjectTeam team = findTeam(projectTeamId);

        // 48시간 초과 체크
        if (team.isConfirmWindowExpired()) {
            throw new ProjectException(ProjectErrorCode.CONFIRM_WINDOW_EXPIRED);
        }

        ProjectTeamMember member = findActiveMember(projectTeamId, userId);

        // 이미 확정한 경우
        if (member.getRecruitmentConfirmStatus() == RecruitmentConfirmStatus.CONFIRMED) {
            throw new ProjectException(ProjectErrorCode.ALREADY_CONFIRMED);
        }

        // PENDING 상태 확인
        if (member.getRecruitmentConfirmStatus() != RecruitmentConfirmStatus.PENDING) {
            throw new ProjectException(ProjectErrorCode.NOT_PENDING_MEMBER);
        }

        // 확정 처리
        member.confirm();
        team.recordFirstConfirmedAt();

        // 전원 확정 여부 확인
        List<ProjectTeamMember> activeMembers = projectTeamMemberRepository
                .findAllByProjectTeamIdAndLeftAtIsNull(projectTeamId);
        boolean allConfirmed = activeMembers.stream()
                .allMatch(m -> m.getRecruitmentConfirmStatus() == RecruitmentConfirmStatus.CONFIRMED);

        if (allConfirmed) {
            // 팀 IN_PROGRESS 전환
            team.start();

            // 단체 채팅방 생성
            chatRoomCommandService.createGroupChatRoom(team);

            // 모든 팀원 카드 RECRUITMENT_COMPLETED 처리
            Long courseId = team.getCourse().getId();
            for (ProjectTeamMember m : activeMembers) {
                userCourseProfileRepository
                        .findByUserIdAndCourseIdAndDeletedAtIsNull(m.getUser().getId(), courseId)
                        .ifPresent(profile -> profile.changeRecruitmentStatus(RecruitmentStatus.RECRUITMENT_COMPLETED));
            }
        }

        return ConfirmResponseDTO.builder()
                .projectTeamId(projectTeamId)
                .myRecruitmentConfirmStatus(RecruitmentConfirmStatus.CONFIRMED.name())
                .allConfirmed(allConfirmed)
                .projectTeamStatus(team.getStatus().name())
                .build();
    }

     // 팀 나가기 - PENDING 상태 (패널티 없음)
     // leftAt 설정
     // 본인 카드 + 나머지 팀원 카드 -> RECRUITING 복구
     // ProjectTeam 유지 (재모집 가능)
    @Override
    @Transactional
    public void leavePending(Long projectTeamId) {
        Long userId = SecurityUtil.requireUserId();

        ProjectTeam team = findTeam(projectTeamId);
        ProjectTeamMember member = findActiveMember(projectTeamId, userId);

        // PENDING 상태 확인
        if (member.getRecruitmentConfirmStatus() != RecruitmentConfirmStatus.PENDING) {
            throw new ProjectException(ProjectErrorCode.NOT_PENDING_MEMBER);
        }

        // 나가기 처리
        member.leave();

        // 모든 팀원(본인 포함) 카드 -> RECRUITING 복구
        Long courseId = team.getCourse().getId();
        List<ProjectTeamMember> activeMembers = projectTeamMemberRepository
                .findAllByProjectTeamIdAndLeftAtIsNull(projectTeamId);

        // 나가는 본인 카드도 복구
        restoreProfileToRecruiting(userId, courseId);

        // 남은 팀원 카드 복구
        for (ProjectTeamMember m : activeMembers) {
            restoreProfileToRecruiting(m.getUser().getId(), courseId);
        }
    }

     // 나가기 요청 생성
     // CONFIRMED 상태 멤버만 가능
     // 팀에 이미 PENDING 요청이 있으면 예외
    @Override
    @Transactional
    public TeamLeaveRequestResponseDTO requestLeave(Long projectTeamId) {
        Long userId = SecurityUtil.requireUserId();

        ProjectTeam team = findTeam(projectTeamId);
        ProjectTeamMember member = findActiveMember(projectTeamId, userId);

        // CONFIRMED 상태 확인
        if (member.getRecruitmentConfirmStatus() != RecruitmentConfirmStatus.CONFIRMED) {
            throw new ProjectException(ProjectErrorCode.NOT_CONFIRMED_MEMBER);
        }

        // 이미 PENDING 나가기 요청이 있는지 확인
        if (teamLeaveRequestRepository.existsByProjectTeamIdAndStatus(
                projectTeamId, TeamLeaveRequestStatus.PENDING)) {
            throw new ProjectException(ProjectErrorCode.LEAVE_REQUEST_ALREADY_EXISTS);
        }

        User user = findUser(userId);
        TeamLeaveRequest leaveRequest = TeamLeaveRequest.create(team, user);
        teamLeaveRequestRepository.save(leaveRequest);

        // 팀원들한테 나가기 요청 알림 해야함.

        return TeamLeaveRequestResponseDTO.builder()
                .teamLeaveRequestId(leaveRequest.getId())
                .projectTeamId(projectTeamId)
                .requesterId(userId)
                .status(leaveRequest.getStatus().name())
                .build();
    }

     // 나가기 인정
     // 현재 유저가 팀의 PENDING 나가기 요청에 동의
     // 본인 요청은 인정 불가
     // 전원 인정 시 -> 요청자 나가기 처리, 요청자 카드 RECRUITING 복구
    @Override
    @Transactional
    public void approveLeave(Long projectTeamId) {
        Long userId = SecurityUtil.requireUserId();

        // 팀 멤버인지 확인 (CONFIRMED)
        findActiveMember(projectTeamId, userId);

        // PENDING 나가기 요청 조회
        TeamLeaveRequest leaveRequest = teamLeaveRequestRepository
                .findByProjectTeamIdAndStatus(projectTeamId, TeamLeaveRequestStatus.PENDING)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.LEAVE_REQUEST_NOT_FOUND));

        // 본인 요청은 인정 불가
        if (leaveRequest.getRequester().getId().equals(userId)) {
            throw new ProjectException(ProjectErrorCode.CANNOT_APPROVE_OWN_LEAVE_REQUEST);
        }

        // 이미 인정했는지 확인
        if (teamLeaveApprovalRepository.existsByTeamLeaveRequestIdAndApproverId(
                leaveRequest.getId(), userId)) {
            throw new ProjectException(ProjectErrorCode.ALREADY_APPROVED_LEAVE);
        }

        // 인정 기록 저장
        User approver = findUser(userId);
        teamLeaveApprovalRepository.save(TeamLeaveApproval.create(leaveRequest, approver));

        // 전원 인정 여부 확인 (요청자를 제외한 나머지 CONFIRMED 멤버 수 vs 인정 수)
        List<ProjectTeamMember> activeMembers = projectTeamMemberRepository
                .findAllByProjectTeamIdAndLeftAtIsNull(projectTeamId);

        long requiredApprovals = activeMembers.stream()
                .filter(m -> m.getRecruitmentConfirmStatus() == RecruitmentConfirmStatus.CONFIRMED)
                .filter(m -> !m.getUser().getId().equals(leaveRequest.getRequester().getId()))
                .count();

        long currentApprovals = teamLeaveApprovalRepository.countByTeamLeaveRequestId(leaveRequest.getId());

        if (currentApprovals >= requiredApprovals) {
            // 전원 인정 → 나가기 처리
            leaveRequest.approve();

            ProjectTeamMember requesterMember = findActiveMember(
                    projectTeamId, leaveRequest.getRequester().getId());
            requesterMember.leave();

            // 요청자 카드 RECRUITING 복구
            Long courseId = leaveRequest.getProjectTeam().getCourse().getId();
            restoreProfileToRecruiting(leaveRequest.getRequester().getId(), courseId);
        }
    }

     // 강제 나가기 (포인트 감점)
     // CONFIRMED 상태 멤버만 가능
     // leftAt 설정
     // 본인 카드 RECRUITING 복구
     // 포인트 감점
    @Override
    @Transactional
    public void leaveForce(Long projectTeamId) {
        Long userId = SecurityUtil.requireUserId();

        ProjectTeam team = findTeam(projectTeamId);
        ProjectTeamMember member = findActiveMember(projectTeamId, userId);

        // CONFIRMED 상태 확인
        if (member.getRecruitmentConfirmStatus() != RecruitmentConfirmStatus.CONFIRMED) {
            throw new ProjectException(ProjectErrorCode.NOT_CONFIRMED_MEMBER);
        }

        // 나가기 처리
        member.leave();

        // 본인 카드 RECRUITING 복구
        Long courseId = team.getCourse().getId();
        restoreProfileToRecruiting(userId, courseId);

        // 포인트 감점 처리 해야함!!!!!!!!!!!!
    }

    // 기타 필요 메서드

    private ProjectTeam findTeam(Long projectTeamId) {
        return projectTeamRepository.findById(projectTeamId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_TEAM_NOT_FOUND));
    }

    private ProjectTeamMember findActiveMember(Long projectTeamId, Long userId) {
        return projectTeamMemberRepository
                .findByProjectTeamIdAndUserIdAndLeftAtIsNull(projectTeamId, userId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.TEAM_MEMBER_NOT_FOUND));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private void restoreProfileToRecruiting(Long userId, Long courseId) {
        userCourseProfileRepository
                .findByUserIdAndCourseIdAndDeletedAtIsNull(userId, courseId)
                .ifPresent(profile -> profile.changeRecruitmentStatus(RecruitmentStatus.RECRUITING));
    }
}
