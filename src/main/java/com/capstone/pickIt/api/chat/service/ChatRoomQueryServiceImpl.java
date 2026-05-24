package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.converter.ChatMessageConverter;
import com.capstone.pickIt.api.chat.converter.CommonCourseConverter;
import com.capstone.pickIt.api.chat.dto.response.ChatMessageResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.ChatRoomResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.CommonCourseResponseDTO;
import com.capstone.pickIt.domain.chat.entity.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomQueryServiceImpl implements ChatRoomQueryService {

    private static final int ROOM_PAGE_SIZE = 15;
    private static final int MESSAGE_PAGE_SIZE = 20;

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
            LocalDateTime cursorLastMessageAt,
            Long cursorChatRoomId
    ) {
        validateCursor(cursorLastMessageAt, cursorChatRoomId);

        List<ChatPart> chatParts = chatPartRepository.findMyChatRooms(
                currentUserId,
                cursorLastMessageAt,
                cursorChatRoomId,
                PageRequest.of(0, ROOM_PAGE_SIZE + 1)
        );

        boolean hasNext = chatParts.size() > ROOM_PAGE_SIZE;

        if (hasNext) {
            chatParts = chatParts.subList(0, ROOM_PAGE_SIZE);
        }

        // 조회 결과가 없으면, 배치 조회를 수행하지 않고 빈 응답 반환
        if (chatParts.isEmpty()) {
            return new ChatRoomResponseDTO.ListResponse(
                    List.of(),
                    null,
                    false
            );
        }

        // 배치 조회를 위한 채팅방 ID 목록 추출
        List<Long> chatRoomIds = chatParts.stream()
                .map(chatPart -> chatPart.getChatRoom().getId())
                .toList();

        Map<Long, Long> unreadCountMap = messageRepository
                .countUnreadMessagesByChatRoomIds(chatRoomIds, currentUserId)
                .stream()
                .collect(Collectors.toMap(
                        MessageRepository.UnreadCountProjection::getChatRoomId,
                        MessageRepository.UnreadCountProjection::getUnreadCount
                ));

        Map<Long, Long> participantCountMap = chatPartRepository
                .countParticipantsByChatRoomIds(chatRoomIds)
                .stream()
                .collect(Collectors.toMap(
                        ChatPartRepository.ParticipantCountProjection::getChatRoomId,
                        ChatPartRepository.ParticipantCountProjection::getParticipantCount
                ));

        List<Long> directChatRoomIds = chatParts.stream()
                .map(ChatPart::getChatRoom)
                .filter(chatRoom -> chatRoom.getChatType() == ChatType.DIRECT)
                .map(ChatRoom::getId)
                .toList();

        Map<Long, ChatRoomResponseDTO.Opponent> opponentMap = directChatRoomIds.isEmpty()
                ? Map.of()
                : chatPartRepository
                .findOpponentsByChatRoomIds(directChatRoomIds, currentUserId)
                .stream()
                .collect(Collectors.toMap(
                        ChatPartRepository.OpponentProjection::getChatRoomId,
                        opponent -> new ChatRoomResponseDTO.Opponent(
                                opponent.getUserId(),
                                opponent.getNickname()
                        )
                ));

        Set<Long> pendingRequestChatRoomIds = new HashSet<>(
                teamRequestRepository.findPendingRequestChatRoomIds(
                        chatRoomIds,
                        currentUserId,
                        TeamRequestStatus.PENDING
                )
        );

        // 배치 조회 결과를 조합하여 채팅방 응답 DTO 생성
        List<ChatRoomResponseDTO.ChatRoomSummary> chatRooms = chatParts.stream()
                .map(chatPart -> toChatRoomSummary(
                        chatPart,
                        unreadCountMap,
                        participantCountMap,
                        opponentMap,
                        pendingRequestChatRoomIds
                ))
                .toList();

        ChatRoomResponseDTO.Cursor nextCursor = hasNext
                ? new ChatRoomResponseDTO.Cursor(
                chatRooms.get(chatRooms.size() - 1).lastMessageAt(),
                chatRooms.get(chatRooms.size() - 1).chatRoomId()
                )
                : null;

        return new ChatRoomResponseDTO.ListResponse(
                chatRooms,
                nextCursor,
                hasNext
        );
    }

    @Override
    public ChatMessageResponseDTO.ListResponse getChatMessages(
            Long currentUserId,
            Long chatRoomId,
            Long cursor
    ) {
        ChatPart myChatPart = chatPartRepository
                .findByChatRoomIdAndUserId(chatRoomId, currentUserId)
                .orElseThrow(() ->
                        new ChatException(ChatErrorCode.NOT_CHAT_ROOM_PARTICIPANT)
                );

        if (myChatPart.isDeleted()) {
            throw new ChatException(ChatErrorCode.NOT_CHAT_ROOM_PARTICIPANT);
        }

        ChatRoom chatRoom = myChatPart.getChatRoom();

        ChatRoomResponseDTO.Opponent opponent =
                getOpponentOrNull(chatRoom, chatRoomId, currentUserId);

        ChatMessageResponseDTO.TeamRequestInfo teamRequest =
                getTeamRequestOrNull(chatRoom, chatRoomId, currentUserId);

        Integer participantCount =
                chatRoom.getChatType() == ChatType.GROUP
                        ? chatPartRepository.countActiveParticipants(chatRoomId)
                        : null;

        List<Message> messages = messageRepository.findMessages(
                chatRoomId,
                cursor,
                PageRequest.of(0, MESSAGE_PAGE_SIZE + 1)
        );

        boolean hasNext = messages.size() > MESSAGE_PAGE_SIZE;

        if (hasNext) {
            messages = messages.subList(0, MESSAGE_PAGE_SIZE);
        }

        if (messages.isEmpty()) {
            return ChatMessageConverter.toListResponse(
                    chatRoom,
                    participantCount,
                    opponent,
                    teamRequest,
                    List.of(),
                    null,
                    false
            );
        }

        List<Long> messageIds = messages.stream()
                .map(Message::getId)
                .toList();

        Map<Long, Long> unreadCountMap = chatPartRepository
                .countUnreadMemberByMessages(chatRoomId, messageIds)
                .stream()
                .collect(Collectors.toMap(
                        ChatPartRepository.UnreadMemberCountProjection::getMessageId,
                        ChatPartRepository.UnreadMemberCountProjection::getUnreadMemberCount
                ));

        List<ChatMessageResponseDTO.MessageSummary> messageSummaries =
                messages.stream()
                        .map(message ->
                                ChatMessageConverter.toMessageSummary(
                                        message,
                                        currentUserId,
                                        unreadCountMap
                                )
                        )
                        .toList();

        Long nextCursor = hasNext
                ? messages.get(messages.size() - 1).getId()
                : null;

        return ChatMessageConverter.toListResponse(
                chatRoom,
                participantCount,
                opponent,
                teamRequest,
                messageSummaries,
                nextCursor,
                hasNext
        );
    }

    private void validateCursor(
            LocalDateTime cursorLastMessageAt,
            Long cursorChatRoomId
    ) {
        if ((cursorLastMessageAt == null) != (cursorChatRoomId == null)) {
            throw new ChatException(ChatErrorCode.INVALID_CURSOR);
        }
    }

    private ChatRoomResponseDTO.ChatRoomSummary toChatRoomSummary(
            ChatPart myChatPart,
            Map<Long, Long> unreadCountMap,
            Map<Long, Long> participantCountMap,
            Map<Long, ChatRoomResponseDTO.Opponent> opponentMap,
            Set<Long> pendingRequestChatRoomIds
    ) {
        ChatRoom chatRoom = myChatPart.getChatRoom();
        Long chatRoomId = chatRoom.getId();

        long unreadCount = unreadCountMap.getOrDefault(chatRoomId, 0L);

        boolean hasPendingTeamRequest =
                chatRoom.getChatType() == ChatType.DIRECT
                        && pendingRequestChatRoomIds.contains(chatRoomId);

        ChatBadgeType badgeType = resolveBadgeType(
                chatRoom,
                hasPendingTeamRequest,
                unreadCount
        );

        ChatRoomResponseDTO.Opponent opponent =
                chatRoom.getChatType() == ChatType.DIRECT
                        ? opponentMap.get(chatRoomId)
                        : null;

        int participantCount = participantCountMap
                .getOrDefault(chatRoomId, 0L)
                .intValue();

        return new ChatRoomResponseDTO.ChatRoomSummary(
                chatRoomId,
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

    private ChatRoomResponseDTO.Opponent getOpponentOrNull(
            ChatRoom chatRoom,
            Long chatRoomId,
            Long currentUserId
    ) {
        if (chatRoom.getChatType() != ChatType.DIRECT) {
            return null;
        }

        ChatPart opponentChatPart = chatPartRepository
                .findOpponent(chatRoomId, currentUserId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_PART_NOT_FOUND));

        return ChatMessageConverter.toOpponent(opponentChatPart);
    }

    private ChatMessageResponseDTO.TeamRequestInfo getTeamRequestOrNull(
            ChatRoom chatRoom,
            Long chatRoomId,
            Long currentUserId
    ) {
        if (chatRoom.getChatType() != ChatType.DIRECT) {
            return null;
        }

        return teamRequestRepository
                .findLatestByChatRoomId(
                        chatRoomId,
                        PageRequest.of(0, 1)
                )
                .stream()
                .findFirst()
                .map(teamRequest -> ChatMessageConverter.toTeamRequestInfo(
                        teamRequest,
                        currentUserId
                ))
                .orElse(null);
    }
}
