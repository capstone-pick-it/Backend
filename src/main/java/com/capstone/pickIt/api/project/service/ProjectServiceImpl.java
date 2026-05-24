package com.capstone.pickIt.api.project.service;

import com.capstone.pickIt.api.checklists.converter.ChecklistConverter;
import com.capstone.pickIt.api.checklists.dto.response.ChecklistItemResponseDTO;
import com.capstone.pickIt.api.project.converter.ProjectConverter;
import com.capstone.pickIt.api.project.dto.response.ProjectDetailResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectListItemResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectMemberListResponseDTO;
import com.capstone.pickIt.api.project.dto.response.ProjectMemberSummaryDTO;
import com.capstone.pickIt.domain.point.entity.Point;
import com.capstone.pickIt.domain.point.repository.PointRepository;
import com.capstone.pickIt.domain.project.entity.ProjectTeam;
import com.capstone.pickIt.domain.project.entity.ProjectTeamMember;
import com.capstone.pickIt.domain.project.entity.ProjectTeamStatus;
import com.capstone.pickIt.domain.project.entity.RecruitmentConfirmStatus;
import com.capstone.pickIt.domain.project.exception.ProjectErrorCode;
import com.capstone.pickIt.domain.project.exception.ProjectException;
import com.capstone.pickIt.domain.project.repository.ChecklistItemRepository;
import com.capstone.pickIt.domain.project.repository.ProjectTeamMemberRepository;
import com.capstone.pickIt.domain.project.repository.ProjectTeamRepository;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectTeamRepository projectTeamRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final PointRepository pointRepository;

    @Override
    public ProjectDetailResponseDTO getProjectDetail(Long projectTeamId) {
        Long currentUserId = SecurityUtil.requireUserId();

        ProjectTeam projectTeam = findProjectTeam(projectTeamId);
        validateActiveProjectMember(projectTeamId, currentUserId);

        List<ProjectMemberSummaryDTO> members = getMemberSummaries(projectTeamId);
        List<ChecklistItemResponseDTO> checklists = checklistItemRepository
                .findActiveItemsOrderByTodoFirstDueDateAscCreatedAtAsc(projectTeam)
                .stream()
                .map(ChecklistConverter::toResponse)
                .toList();

        return ProjectConverter.toDetailResponse(projectTeam, members, checklists);
    }

    @Override
    public ProjectMemberListResponseDTO getProjectMembers(Long projectTeamId) {
        Long currentUserId = SecurityUtil.requireUserId();

        findProjectTeam(projectTeamId);
        validateActiveProjectMember(projectTeamId, currentUserId);

        List<ProjectMemberSummaryDTO> members = getMemberSummaries(projectTeamId);

        return ProjectConverter.toMemberListResponse(projectTeamId, members);
    }

    private List<ProjectMemberSummaryDTO> getMemberSummaries(Long projectTeamId) {
        List<ProjectTeamMember> members = projectTeamMemberRepository
                .findActiveConfirmedMembersWithUser(
                        projectTeamId,
                        RecruitmentConfirmStatus.CONFIRMED
                );

        List<Long> userIds = members.stream()
                .map(member -> member.getUser().getId())
                .toList();

        Map<Long, Point> pointMap = pointRepository.findAllByUserIdIn(userIds)
                .stream()
                .collect(Collectors.toMap(
                        point -> point.getUser().getId(),
                        Function.identity()
                ));

        return members.stream()
                .map(member -> ProjectConverter.toMemberSummary(member, pointMap))
                .toList();
    }

    @Override
    public List<ProjectListItemResponseDTO> getUserProjectList(ProjectTeamStatus status) {
        Long currentUserId = SecurityUtil.requireUserId();

        List<ProjectTeamMember> memberships = projectTeamMemberRepository
                .findActiveConfirmedMembershipsWithTeamAndCourse(currentUserId, status);

        if (memberships.isEmpty()) {
            return List.of();
        }

        List<Long> projectTeamIds = memberships.stream()
                .map(m -> m.getProjectTeam().getId())
                .toList();

        Map<Long, List<String>> memberNamesMap = projectTeamMemberRepository
                .findActiveConfirmedMembersWithUserByProjectTeamIds(projectTeamIds)
                .stream()
                .collect(Collectors.groupingBy(
                        m -> m.getProjectTeam().getId(),
                        Collectors.mapping(m -> m.getUser().getNickname(), Collectors.toList())
                ));

        return memberships.stream()
                .map(m -> {
                    ProjectTeam team = m.getProjectTeam();
                    List<String> names = memberNamesMap.getOrDefault(team.getId(), List.of());
                    return ProjectConverter.toListItem(team, names);
                })
                .toList();
    }

    private ProjectTeam findProjectTeam(Long projectTeamId) {
        return projectTeamRepository.findById(projectTeamId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_TEAM_NOT_FOUND));
    }

    private void validateActiveProjectMember(Long projectTeamId, Long userId) {
        boolean exists = projectTeamMemberRepository.existsActiveConfirmedMember(
                projectTeamId,
                userId,
                RecruitmentConfirmStatus.CONFIRMED
        );

        if (!exists) {
            throw new ProjectException(ProjectErrorCode.NOT_PROJECT_MEMBER);
        }
    }
}