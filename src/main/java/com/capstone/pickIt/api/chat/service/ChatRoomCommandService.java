package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.dto.request.ChatMessageRequestDTO;
import com.capstone.pickIt.api.chat.dto.request.TeamRequestCreateRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.ChatMessageResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.ChatRoomResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.DirectChatRoomResponseDTO;
import com.capstone.pickIt.api.chat.dto.request.DirectChatRoomCreateRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.TeamRequestResponseDTO;
import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import com.capstone.pickIt.domain.project.entity.ProjectTeam;

public interface ChatRoomCommandService {

    DirectChatRoomResponseDTO.CreateOrEnter createOrEnterDirectChatRoom(
            Long currentUserId,
            DirectChatRoomCreateRequestDTO request
    );

    ChatRoom createGroupChatRoom(
            ProjectTeam projectTeam
    );

    ChatRoomResponseDTO.LeaveResponse leaveChatRoom(
            Long currentUserId,
            Long chatRoomId
    );

    ChatMessageResponseDTO.ReadUpdateResponse updateLastReadMessage(
            Long currentUserId,
            Long chatRoomId,
            ChatMessageRequestDTO.ReadUpdateRequest request
    );

    TeamRequestResponseDTO.Create createTeamRequest(
            Long currentUserId,
            Long chatRoomId,
            TeamRequestCreateRequestDTO request
    );

    TeamRequestResponseDTO.Respond acceptTeamRequest(
            Long currentUserId,
            Long chatRoomId,
            Long teamRequestId
    );
}
