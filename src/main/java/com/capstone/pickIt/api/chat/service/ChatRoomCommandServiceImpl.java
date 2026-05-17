package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.converter.TeamRequestConverter;
import com.capstone.pickIt.api.chat.dto.request.TeamRequestCreateRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.TeamRequestResponseDTO;
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
import com.capstone.pickIt.domain.course.repository.CourseRepository;
import com.capstone.pickIt.domain.course.repository.UserCourseRepository;
import com.capstone.pickIt.domain.project.entity.TeamRequest;
import com.capstone.pickIt.domain.project.entity.TeamRequestStatus;
import com.capstone.pickIt.domain.project.repository.TeamRequestRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

        try {
            TeamRequest teamRequest = TeamRequest.create(
                    chatRoom,
                    course,
                    sender,
                    receiver
            );

            teamRequestRepository.saveAndFlush(teamRequest);

            // TODO: WebSocket 연결 후 TEAM_REQUEST_CREATED 이벤트 브로드캐스트 구현

            return TeamRequestConverter.toCreateResponse(teamRequest, currentUserId);

        } catch (DataIntegrityViolationException e) {
            throw new ChatException(ChatErrorCode.PENDING_TEAM_REQUEST_ALREADY_EXISTS);
        }
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
}
