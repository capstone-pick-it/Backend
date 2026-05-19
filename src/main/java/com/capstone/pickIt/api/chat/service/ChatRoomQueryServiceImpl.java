package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.converter.CommonCourseConverter;
import com.capstone.pickIt.api.chat.dto.response.CommonCourseResponseDTO;
import com.capstone.pickIt.domain.chat.entity.ChatPart;
import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import com.capstone.pickIt.domain.chat.entity.ChatType;
import com.capstone.pickIt.domain.chat.exception.ChatErrorCode;
import com.capstone.pickIt.domain.chat.exception.ChatException;
import com.capstone.pickIt.domain.chat.repository.ChatPartRepository;
import com.capstone.pickIt.domain.chat.repository.ChatRoomRepository;
import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.domain.course.repository.UserCourseProfileRepository;
import com.capstone.pickIt.domain.project.entity.TeamRequestStatus;
import com.capstone.pickIt.domain.project.repository.TeamRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomQueryServiceImpl implements ChatRoomQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatPartRepository chatPartRepository;
    private final UserCourseProfileRepository userCourseProfileRepository;
    private final TeamRequestRepository teamRequestRepository;

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
}
