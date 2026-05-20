package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.dto.request.TeamRequestCreateRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.DirectChatRoomResponseDTO;
import com.capstone.pickIt.api.chat.dto.request.DirectChatRoomCreateRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.TeamRequestResponseDTO;

public interface ChatRoomCommandService {
    DirectChatRoomResponseDTO.CreateOrEnter createOrEnterDirectChatRoom(
            Long currentUserId,
            DirectChatRoomCreateRequestDTO request
    );

    TeamRequestResponseDTO.Create createTeamRequest(
            Long currentUserId,
            Long chatRoomId,
            TeamRequestCreateRequestDTO request
    );
}
