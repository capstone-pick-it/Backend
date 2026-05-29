package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.converter.ChatRoomEventConverter;
import com.capstone.pickIt.api.chat.dto.request.ChatMessageSendRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.ChatNotificationResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.ChatRoomEventResponseDTO;
import com.capstone.pickIt.api.chat.event.ChatRoomBroadcastEvent;
import com.capstone.pickIt.api.chat.event.ChatUserNotificationEvent;
import com.capstone.pickIt.domain.chat.entity.*;
import com.capstone.pickIt.domain.chat.exception.ChatErrorCode;
import com.capstone.pickIt.domain.chat.exception.ChatException;
import com.capstone.pickIt.domain.chat.repository.ChatPartRepository;
import com.capstone.pickIt.domain.chat.repository.ChatRoomRepository;
import com.capstone.pickIt.domain.chat.repository.MessageFileRepository;
import com.capstone.pickIt.domain.chat.repository.MessageRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatMessageCommandServiceImpl implements ChatMessageCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatPartRepository chatPartRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageFileRepository messageFileRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void sendMessage(Long currentUserId, ChatMessageSendRequestDTO request) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.chatRoomId())
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        ChatPart chatPart = chatPartRepository
                .findByChatRoomIdAndUserId(chatRoom.getId(), currentUserId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.NOT_CHAT_ROOM_PARTICIPANT));

        if (chatPart.isDeleted()) {
            throw new ChatException(ChatErrorCode.NOT_CHAT_ROOM_PARTICIPANT);
        }

        User sender = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CURRENT_USER_NOT_FOUND));

        validateMessagePayload(request);

        Message message = createMessage(chatRoom, sender, request);
        messageRepository.saveAndFlush(message);

        List<MessageFile> files = saveMessageFiles(message, request);

        chatRoom.updateLastMessage(message);

        int unreadMemberCount = Math.max(
                0,
                chatPartRepository.countActiveParticipants(chatRoom.getId()) - 1
        );

        ChatRoomEventResponseDTO.ChatRoomEvent response =
                ChatRoomEventConverter.toMessageEvent(
                        chatRoom,
                        message,
                        files,
                        unreadMemberCount
                );

        log.info(
                "Publishing chat message event: chatRoomId={}, chatType={}, senderId={}",
                chatRoom.getId(),
                chatRoom.getChatType(),
                currentUserId
        );

        // 채팅방 메시지 브로드캐스트용 이벤트 발행
        eventPublisher.publishEvent(
                new ChatRoomBroadcastEvent(chatRoom.getId(), response)
        );

        // 채팅방 참여자별 채팅 목록 갱신 알림 이벤트 발행
        List<ChatPart> participants =
                chatPartRepository.findActiveParticipantsWithUserByChatRoomId(chatRoom.getId());

        for (ChatPart participant : participants) {
            Long receiverId = participant.getUser().getId();

            // 발신자는 제외
            if (receiverId.equals(currentUserId)) {
                continue;
            }

            // 수신자 기준 안 읽은 메시지 개수 계산
            Long unreadCount = messageRepository.countUnreadMessagesByChatRoomId(
                    chatRoom.getId(),
                    receiverId
            );

            ChatNotificationResponseDTO.ChatRoomNotification notification =
                    new ChatNotificationResponseDTO.ChatRoomNotification(
                            "CHAT_MESSAGE_RECEIVED",
                            chatRoom.getId(),
                            chatRoom.getChatType(),
                            message.getId(),
                            message.getMessageType(),
                            message.getContent(),
                            message.getCreatedAt(),
                            sender.getId(),
                            sender.getNickname(),
                            unreadCount
                    );

            log.info(
                    "Publishing chat user notification: chatRoomId={}, receiverId={}, senderId={}",
                    chatRoom.getId(),
                    receiverId,
                    currentUserId
            );

            // 채팅 목록(lastMessage, unreadCount) 갱신용 개인 알림 이벤트 발행
            eventPublisher.publishEvent(
                    new ChatUserNotificationEvent(receiverId, notification)
            );
        }
    }

    private void validateMessagePayload(ChatMessageSendRequestDTO request) {
        switch (request.messageType()) {

            case TEXT -> {
                if (request.content() == null || request.content().isBlank()) {
                    throw new ChatException(ChatErrorCode.INVALID_MESSAGE_CONTENT);
                }

                if (request.files() != null && !request.files().isEmpty()) {
                    throw new ChatException(ChatErrorCode.MESSAGE_FILE_NOT_ALLOWED);
                }
            }

            case FILE -> {
                if (request.content() != null && !request.content().isBlank()) {
                    throw new ChatException(ChatErrorCode.MESSAGE_CONTENT_NOT_ALLOWED);
                }

                if (request.files() == null || request.files().isEmpty()) {
                    throw new ChatException(ChatErrorCode.MESSAGE_FILE_REQUIRED);
                }
            }

            default ->
                    throw new ChatException(ChatErrorCode.INVALID_MESSAGE_TYPE);
        }
    }

    private Message createMessage(
            ChatRoom chatRoom,
            User sender,
            ChatMessageSendRequestDTO request
    ) {
        return switch (request.messageType()) {

            case TEXT ->
                    Message.createTextMessage(
                            chatRoom,
                            sender,
                            request.content()
                    );

            case FILE ->
                    Message.createFileMessage(
                            chatRoom,
                            sender
                    );

            default ->
                    throw new ChatException(ChatErrorCode.INVALID_MESSAGE_TYPE);
        };
    }

    private List<MessageFile> saveMessageFiles(
            Message message,
            ChatMessageSendRequestDTO request
    ) {
        if (request.messageType() != MessageType.FILE) {
            return List.of();
        }

        List<MessageFile> files = request.files().stream()
                .map(file -> MessageFile.create(
                        message,
                        file.fileUrl(),
                        file.fileName(),
                        file.fileSize(),
                        file.contentType()
                ))
                .toList();

        return messageFileRepository.saveAll(files);
    }
}
