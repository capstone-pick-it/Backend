package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.converter.ChatRoomEventConverter;
import com.capstone.pickIt.api.chat.dto.request.ChatMessageSendRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.ChatRoomEventResponseDTO;
import com.capstone.pickIt.api.chat.event.ChatRoomBroadcastEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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

        eventPublisher.publishEvent(
                new ChatRoomBroadcastEvent(chatRoom.getId(), response)
        );
    }

    private void validateMessagePayload(ChatMessageSendRequestDTO request) {
        if (request.messageType() == MessageType.TEXT) {
            if (request.content() == null || request.content().isBlank()) {
                throw new ChatException(ChatErrorCode.INVALID_MESSAGE_CONTENT);
            }

            if (request.files() != null && !request.files().isEmpty()) {
                throw new ChatException(ChatErrorCode.MESSAGE_FILE_NOT_ALLOWED);
            }
        }

        if (request.messageType() == MessageType.FILE) {
            if (request.content() != null && !request.content().isBlank()) {
                throw new ChatException(ChatErrorCode.MESSAGE_CONTENT_NOT_ALLOWED);
            }

            if (request.files() == null || request.files().isEmpty()) {
                throw new ChatException(ChatErrorCode.MESSAGE_FILE_REQUIRED);
            }
        }
    }

    private Message createMessage(
            ChatRoom chatRoom,
            User sender,
            ChatMessageSendRequestDTO request
    ) {
        if (request.messageType() == MessageType.TEXT) {
            return Message.createTextMessage(
                    chatRoom,
                    sender,
                    request.content()
            );
        }

        return Message.createFileMessage(chatRoom, sender);
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
