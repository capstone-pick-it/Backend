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
import com.capstone.pickIt.domain.chat.repository.*;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final ChatFileService chatFileService;

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

        // 메시지 타입별 요청 데이터 검증
        validateMessagePayload(request);

        // 메시지 저장
        Message message = createMessage(chatRoom, sender, request);
        messageRepository.saveAndFlush(message);

        // 첨부파일 저장
        List<MessageFile> files = saveMessageFiles(message, request);
        List<ChatRoomEventResponseDTO.FilePayload> filePayloads = files.stream()
                .map(file -> new ChatRoomEventResponseDTO.FilePayload(
                        file.getId(),
                        file.getFileName(),
                        chatFileService.createSignedUrl(file.getFileUrl()),
                        file.getFileSize(),
                        file.getContentType()
                ))
                .toList();

        // 채팅방 마지막 메시지 정보 갱신
        chatRoom.updateLastMessage(message);

        // 발신자를 제외한 안 읽은 참여자 수 계산
        int unreadMemberCount = Math.max(
                0,
                chatPartRepository.countActiveParticipants(chatRoom.getId()) - 1
        );

        ChatRoomEventResponseDTO.ChatRoomEvent response =
                ChatRoomEventConverter.toMessageEvent(
                        chatRoom,
                        message,
                        filePayloads,
                        unreadMemberCount
                );

        log.info(
                "Publishing chat message event: chatRoomId={}, chatType={}, senderId={}",
                chatRoom.getId(),
                chatRoom.getChatType(),
                currentUserId
        );

        /*
         * 채팅방 메시지 브로드캐스트용 이벤트 발행
         */
        eventPublisher.publishEvent(
                new ChatRoomBroadcastEvent(chatRoom.getId(), response)
        );

        /*
         * 채팅방 참여자별 채팅 목록 갱신 알림 이벤트 발행
         */
        List<ChatPart> participants =
                chatPartRepository.findActiveParticipantsWithUserByChatRoomId(chatRoom.getId());

        // 수신자별 안 읽은 메시지 개수 한 번에 조회
        Map<Long, Long> unreadCountMap =
                messageRepository.countUnreadMessagesByUsersInChatRoom(
                                chatRoom.getId(),
                                currentUserId
                        )
                        .stream()
                        .collect(Collectors.toMap(
                                UnreadCountByUserProjection::getUserId,
                                UnreadCountByUserProjection::getUnreadCount
                        ));

        for (ChatPart participant : participants) {
            Long receiverId = participant.getUser().getId();

            // 발신자는 제외
            if (receiverId.equals(currentUserId)) {
                continue;
            }

            Long unreadCount = unreadCountMap.getOrDefault(receiverId, 0L);

            String lastMessage = message.getMessageType() == MessageType.FILE
                    ? "파일을 보냈습니다."
                    : message.getContent();

            ChatNotificationResponseDTO.ChatRoomNotification notification =
                    new ChatNotificationResponseDTO.ChatRoomNotification(
                            "CHAT_MESSAGE_RECEIVED",
                            chatRoom.getId(),
                            chatRoom.getChatType(),
                            message.getId(),
                            message.getMessageType(),
                            lastMessage,
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
                        file.fileUrl(), // GCS objectName
                        file.fileName(), // 원본 파일명
                        file.fileSize(),
                        file.contentType()
                ))
                .toList();

        return messageFileRepository.saveAll(files);
    }
}
