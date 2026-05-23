package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.converter.CommonCourseConverter;
import com.capstone.pickIt.api.chat.dto.response.ChatRoomResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.CommonCourseResponseDTO;
import com.capstone.pickIt.domain.chat.entity.ChatBadgeType;
import com.capstone.pickIt.domain.chat.entity.ChatPart;
import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import com.capstone.pickIt.domain.chat.entity.ChatType;
import com.capstone.pickIt.domain.chat.exception.ChatErrorCode;
import com.capstone.pickIt.domain.chat.exception.ChatException;
import com.capstone.pickIt.domain.chat.repository.ChatPartRepository;
import com.capstone.pickIt.domain.chat.repository.ChatRoomRepository;
import com.capstone.pickIt.domain.chat.repository.MessageRepository;
import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.domain.course.repository.UserCourseProfileRepository;
import com.capstone.pickIt.domain.project.entity.TeamRequestStatus;
import com.capstone.pickIt.domain.project.repository.TeamRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomQueryServiceImpl implements ChatRoomQueryService {

    private static final int PAGE_SIZE = 15;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatPartRepository chatPartRepository;
    private final UserCourseProfileRepository userCourseProfileRepository;
    private final TeamRequestRepository teamRequestRepository;
    private final MessageRepository messageRepository;

    @Override
    public CommonCourseResponseDTO.CommonCourseList getCommonCourses(
            Long currentUserId,
            Long chatRoomId
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

        ChatPart opponentChatPart = chatPartRepository
                .findOpponent(chatRoomId, currentUserId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_PART_NOT_FOUND));

        Long opponentUserId = opponentChatPart.getUser().getId();

        boolean existsPendingForReceiver =
                teamRequestRepository.existsBySenderIdAndReceiverIdAndTeamRequestStatus(
                        currentUserId,
                        opponentUserId,
                        TeamRequestStatus.PENDING
                );

        if (existsPendingForReceiver) {
            return new CommonCourseResponseDTO.CommonCourseList(List.of());
        }

        List<Course> commonCourses = userCourseProfileRepository.findRequestableCommonCourses(
                currentUserId,
                opponentChatPart.getUser().getId()
        );

        return CommonCourseConverter.toCommonCourseList(commonCourses);
    }

    @Override
    public ChatRoomResponseDTO.ListResponse getMyChatRooms(
            Long currentUserId,
            Long cursor
    ) {
        LocalDateTime cursorLastMessageAt = null;

        if (cursor != null) {
            cursorLastMessageAt = chatRoomRepository.findById(cursor)
                    .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND))
                    .getLastMessageAt();
        }

        List<ChatPart> chatParts = chatPartRepository.findMyChatRooms(
                currentUserId,
                cursor,
                cursorLastMessageAt,
                PageRequest.of(0, PAGE_SIZE + 1)
        );

        boolean hasNext = chatParts.size() > PAGE_SIZE;

        if (hasNext) {
            chatParts = chatParts.subList(0, PAGE_SIZE);
        }

        List<ChatRoomResponseDTO.ChatRoomSummary> chatRooms = chatParts.stream()
                .map(chatPart -> toChatRoomSummary(chatPart, currentUserId))
                .toList();

        Long nextCursor = hasNext
                ? chatRooms.get(chatRooms.size() - 1).chatRoomId()
                : null;

        return new ChatRoomResponseDTO.ListResponse(
                chatRooms,
                nextCursor,
                hasNext
        );
    }

    private ChatRoomResponseDTO.ChatRoomSummary toChatRoomSummary(
            ChatPart myChatPart,
            Long currentUserId
    ) {
        ChatRoom chatRoom = myChatPart.getChatRoom();

        long unreadCount = getUnreadCount(chatRoom, myChatPart, currentUserId);
        boolean hasPendingTeamRequest = hasPendingTeamRequest(chatRoom, currentUserId);

        ChatBadgeType badgeType = resolveBadgeType(
                chatRoom,
                hasPendingTeamRequest,
                unreadCount
        );

        ChatRoomResponseDTO.Opponent opponent = null;

        if (chatRoom.getChatType() == ChatType.DIRECT) {
            ChatPart opponentChatPart = chatPartRepository
                    .findOpponent(chatRoom.getId(), currentUserId)
                    .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_PART_NOT_FOUND));

            opponent = new ChatRoomResponseDTO.Opponent(
                    opponentChatPart.getUser().getId(),
                    opponentChatPart.getUser().getNickname()
            );
        }

        int participantCount = chatPartRepository
                .countByChatRoomIdAndDeletedAtIsNull(chatRoom.getId());

        return new ChatRoomResponseDTO.ChatRoomSummary(
                chatRoom.getId(),
                chatRoom.getChatType(),
                opponent,
                chatRoom.getRoomName(),
                participantCount,
                getLastMessageContent(chatRoom),
                chatRoom.getLastMessageAt(),
                unreadCount,
                badgeType
        );
    }

    private long getUnreadCount(
            ChatRoom chatRoom,
            ChatPart myChatPart,
            Long currentUserId
    ) {
        Long lastReadMessageId = myChatPart.getLastReadMessage() == null
                ? null
                : myChatPart.getLastReadMessage().getId();

        return messageRepository.countUnreadMessages(
                chatRoom.getId(),
                currentUserId,
                lastReadMessageId
        );
    }

    private boolean hasPendingTeamRequest(
            ChatRoom chatRoom,
            Long currentUserId
    ) {
        if (chatRoom.getChatType() != ChatType.DIRECT) {
            return false;
        }

        return teamRequestRepository.existsByChatRoomIdAndReceiverIdAndTeamRequestStatus(
                chatRoom.getId(),
                currentUserId,
                TeamRequestStatus.PENDING
        );
    }

    private ChatBadgeType resolveBadgeType(
            ChatRoom chatRoom,
            boolean hasPendingTeamRequest,
            long unreadCount
    ) {
        if (chatRoom.getChatType() == ChatType.DIRECT && hasPendingTeamRequest) {
            return ChatBadgeType.TEAM_REQUEST;
        }

        if (unreadCount > 0) {
            return ChatBadgeType.UNREAD_MESSAGE;
        }

        return ChatBadgeType.NONE;
    }

    private String getLastMessageContent(ChatRoom chatRoom) {
        if (chatRoom.getLastMessage() == null) {
            return null;
        }

        return chatRoom.getLastMessage().getContent();
    }
}
