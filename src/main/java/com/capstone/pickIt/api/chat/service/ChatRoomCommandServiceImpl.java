package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.domain.chat.exception.ChatErrorCode;
import com.capstone.pickIt.api.chat.converter.ChatRoomConverter;
import com.capstone.pickIt.api.chat.dto.request.DirectChatRoomCreateRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.DirectChatRoomResponseDTO;
import com.capstone.pickIt.domain.chat.exception.ChatException;
import com.capstone.pickIt.domain.chat.entity.ChatPart;
import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import com.capstone.pickIt.domain.chat.repository.ChatPartRepository;
import com.capstone.pickIt.domain.chat.repository.ChatRoomRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

        Optional<ChatRoom> existingChatRoom =
                chatRoomRepository.findDirectChatRoomByUserIds(currentUserId, targetUserId);

        if (existingChatRoom.isPresent()) {
            ChatRoom chatRoom = existingChatRoom.get();

            ChatPart currentChatPart = chatPartRepository
                    .findByChatRoomIdAndUserId(chatRoom.getId(), currentUserId)
                    .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_PART_NOT_FOUND));

            ChatPart targetChatPart = chatPartRepository
                    .findByChatRoomIdAndUserId(chatRoom.getId(), targetUserId)
                    .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_PART_NOT_FOUND));

            currentChatPart.restore();
            targetChatPart.restore();

            return ChatRoomConverter.toCreateOrEnterResponse(chatRoom, targetUser, false);
        }

        ChatRoom chatRoom = ChatRoom.createDirectRoom();

        chatRoom.addParticipant(currentUser);
        chatRoom.addParticipant(targetUser);

        chatRoomRepository.save(chatRoom);

        return ChatRoomConverter.toCreateOrEnterResponse(chatRoom, targetUser, true);
    }
}
