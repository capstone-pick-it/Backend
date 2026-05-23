package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.converter.ChatRoomEventConverter;
import com.capstone.pickIt.api.chat.converter.TeamRequestConverter;
import com.capstone.pickIt.api.chat.dto.request.TeamRequestCreateRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.ChatRoomEventResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.TeamRequestResponseDTO;
import com.capstone.pickIt.api.chat.event.ChatRoomBroadcastEvent;
import com.capstone.pickIt.api.point.dto.response.PointResponseDTO;
import com.capstone.pickIt.api.point.service.PointService;
import com.capstone.pickIt.domain.chat.entity.ChatType;
import com.capstone.pickIt.domain.chat.exception.ChatErrorCode;
import com.capstone.pickIt.api.chat.converter.ChatRoomConverter;
import com.capstone.pickIt.api.chat.dto.request.DirectChatRoomCreateRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.DirectChatRoomResponseDTO;
import com.capstone.pickIt.domain.chat.exception.ChatException;
import com.capstone.pickIt.domain.chat.entity.ChatPart;
import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import com.capstone.pickIt.domain.chat.repository.ChatPartRepository;
import com.capstone.pickIt.domain.chat.repository.ChatRoomRepository;
import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.domain.course.entity.RecruitmentStatus;
import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import com.capstone.pickIt.domain.course.repository.CourseRepository;
import com.capstone.pickIt.domain.course.repository.UserCourseProfileRepository;
import com.capstone.pickIt.domain.course.repository.UserCourseRepository;
import com.capstone.pickIt.domain.project.entity.*;
import com.capstone.pickIt.domain.project.repository.ProjectTeamMemberRepository;
import com.capstone.pickIt.domain.project.repository.ProjectTeamRepository;
import com.capstone.pickIt.domain.project.repository.TeamRequestRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.capstone.pickIt.domain.point.policy.PointPolicy.PROJECT_REQUIRED_POINT;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomCommandServiceImpl implements ChatRoomCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatPartRepository chatPartRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final TeamRequestRepository teamRequestRepository;
    private final ProjectTeamRepository projectTeamRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final UserCourseProfileRepository userCourseProfileRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PointService pointService;

    @Override
    public DirectChatRoomResponseDTO.CreateOrEnter createOrEnterDirectChatRoom(
            Long currentUserId,
            DirectChatRoomCreateRequestDTO request
    ) {
        Long targetUserId = request.targetUserId();

        if (currentUserId.equals(targetUserId)) {
            throw new ChatException(ChatErrorCode.CANNOT_CHAT_WITH_SELF);
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CURRENT_USER_NOT_FOUND));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.TARGET_USER_NOT_FOUND));

        Long minUserId = Math.min(currentUserId, targetUserId);
        Long maxUserId = Math.max(currentUserId, targetUserId);

        Optional<ChatRoom> existingChatRoom =
                chatRoomRepository.findDirectChatRoomByUserIds(minUserId, maxUserId);

        if (existingChatRoom.isPresent()) {
            return restoreAndConvert(existingChatRoom.get(), currentUserId, targetUser, false);
        }

        try {
            ChatRoom chatRoom = ChatRoom.createDirectRoom(currentUser, targetUser);

            chatRoom.addParticipant(currentUser);
            chatRoom.addParticipant(targetUser);

            chatRoomRepository.saveAndFlush(chatRoom);

            return ChatRoomConverter.toCreateOrEnterResponse(chatRoom, targetUser, true);

        } catch (DataIntegrityViolationException e) {
            ChatRoom chatRoom = chatRoomRepository
                    .findDirectChatRoomByUserIds(minUserId, maxUserId)
                    .orElseThrow(() -> e);

            return restoreAndConvert(chatRoom, currentUserId, targetUser, false);
        }
    }

    @Override
    public TeamRequestResponseDTO.Create createTeamRequest(
            Long currentUserId,
            Long chatRoomId,
            TeamRequestCreateRequestDTO request
    ) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        if (chatRoom.getChatType() != ChatType.DIRECT) {
            throw new ChatException(ChatErrorCode.ONLY_DIRECT_CHAT_CAN_REQUEST_TEAM);
        }

        ChatPart currentChatPart = chatPartRepository
                .findByChatRoomIdAndUserId(chatRoomId, currentUserId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.NOT_CHAT_ROOM_PARTICIPANT));

        if (currentChatPart.isDeleted()) {
            throw new ChatException(ChatErrorCode.NOT_CHAT_ROOM_PARTICIPANT);
        }

        ChatPart receiverChatPart = chatPartRepository
                .findOpponent(chatRoomId, currentUserId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_PART_NOT_FOUND));

        User sender = currentChatPart.getUser();
        User receiver = receiverChatPart.getUser();

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ChatException(ChatErrorCode.COURSE_NOT_FOUND));

        boolean isCommonCourse = userCourseRepository.existsCommonCourse(
                sender.getId(),
                receiver.getId(),
                course.getId()
        );

        if (!isCommonCourse) {
            throw new ChatException(ChatErrorCode.NOT_COMMON_COURSE);
        }

        boolean existsPendingForCourse =
                teamRequestRepository.existsBySenderIdAndCourseIdAndTeamRequestStatus(
                        sender.getId(),
                        course.getId(),
                        TeamRequestStatus.PENDING
                );

        if (existsPendingForCourse) {
            throw new ChatException(ChatErrorCode.PENDING_REQUEST_EXISTS_FOR_COURSE);
        }

        boolean existsPendingForReceiver =
                teamRequestRepository.existsBySenderIdAndReceiverIdAndTeamRequestStatus(
                        sender.getId(),
                        receiver.getId(),
                        TeamRequestStatus.PENDING
                );

        if (existsPendingForReceiver) {
            throw new ChatException(ChatErrorCode.PENDING_REQUEST_EXISTS_FOR_RECEIVER);
        }

        PointResponseDTO point = pointService.refreshAndGetPoint(currentUserId);

        if (point.balance() < PROJECT_REQUIRED_POINT) {
            throw new ChatException(ChatErrorCode.INSUFFICIENT_POINT);
        }

        try {
            TeamRequest teamRequest = TeamRequest.create(
                    chatRoom,
                    course,
                    sender,
                    receiver
            );

            teamRequestRepository.saveAndFlush(teamRequest);

            ChatRoomEventResponseDTO.ChatRoomEvent event =
                    ChatRoomEventConverter.toTeamRequestCreatedEvent(teamRequest);

            eventPublisher.publishEvent(
                    new ChatRoomBroadcastEvent(chatRoom.getId(), event)
            );

            return TeamRequestConverter.toCreateResponse(teamRequest, currentUserId);

        } catch (DataIntegrityViolationException e) {
            throw new ChatException(ChatErrorCode.PENDING_TEAM_REQUEST_ALREADY_EXISTS);
        }
    }

    @Override
    public TeamRequestResponseDTO.Respond acceptTeamRequest(
            Long currentUserId,
            Long chatRoomId,
            Long teamRequestId
    ) {
        TeamRequest teamRequest = teamRequestRepository.findById(teamRequestId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.TEAM_REQUEST_NOT_FOUND));

        if (!teamRequest.getChatRoom().getId().equals(chatRoomId)) {
            throw new ChatException(ChatErrorCode.TEAM_REQUEST_NOT_FOUND);
        }

        if (!teamRequest.isReceiver(currentUserId)) {
            throw new ChatException(ChatErrorCode.NOT_TEAM_REQUEST_RECEIVER);
        }

        if (!teamRequest.isPending()) {
            throw new ChatException(ChatErrorCode.TEAM_REQUEST_NOT_PENDING);
        }

        Course course = teamRequest.getCourse();
        User sender = teamRequest.getSender();
        User receiver = teamRequest.getReceiver();

        ProjectTeam projectTeam = projectTeamRepository
                .findRecruitingTeamByCourseAndMember(
                        course.getId(),
                        sender.getId(),
                        receiver.getId()
                )
                .orElseGet(() -> projectTeamRepository.save(
                        ProjectTeam.createRecruitingTeam(course)
                ));

        addPendingTeamMemberIfNotExists(projectTeam, sender);
        addPendingTeamMemberIfNotExists(projectTeam, receiver);

        updateCourseProfileToConfirmPending(sender.getId(), course.getId());
        updateCourseProfileToConfirmPending(receiver.getId(), course.getId());

        teamRequest.accept();

        ChatRoomEventResponseDTO.ChatRoomEvent event =
                ChatRoomEventConverter.toTeamRequestAcceptedEvent(teamRequest);

        eventPublisher.publishEvent(
                new ChatRoomBroadcastEvent(chatRoomId, event)
        );

        return TeamRequestConverter.toRespondResponse(teamRequest);
    }

    private DirectChatRoomResponseDTO.CreateOrEnter restoreAndConvert(
            ChatRoom chatRoom,
            Long currentUserId,
            User targetUser,
            boolean isNew
    ) {
        ChatPart currentChatPart = chatPartRepository
                .findByChatRoomIdAndUserId(chatRoom.getId(), currentUserId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_PART_NOT_FOUND));

        currentChatPart.restore();

        return ChatRoomConverter.toCreateOrEnterResponse(chatRoom, targetUser, isNew);
    }

    private void addPendingTeamMemberIfNotExists(
            ProjectTeam projectTeam,
            User user
    ) {
        boolean existsMember = projectTeamMemberRepository
                .existsByProjectTeamIdAndUserId(projectTeam.getId(), user.getId());

        if (existsMember) {
            return;
        }

        try {
            ProjectTeamMember projectTeamMember =
                    ProjectTeamMember.createPendingMember(projectTeam, user);

            projectTeamMemberRepository.saveAndFlush(projectTeamMember);

        } catch (DataIntegrityViolationException e) {
            // 동시에 같은 팀 멤버가 추가된 경우 무시
        }
    }

    private void updateCourseProfileToConfirmPending(
            Long userId,
            Long courseId
    ) {
        UserCourseProfile userCourseProfile = userCourseProfileRepository
                .findByUserIdAndCourseIdAndDeletedAtIsNull(userId, courseId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.USER_COURSE_PROFILE_NOT_FOUND));

        if (userCourseProfile.getRecruitmentStatus() != RecruitmentStatus.RECRUITING) {
            throw new ChatException(ChatErrorCode.INVALID_RECRUITMENT_STATUS);
        }

        userCourseProfile.changeRecruitmentStatus(RecruitmentStatus.CONFIRM_PENDING);
    }
}
