package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.dto.response.DirectChatRoomResponseDTO;
import com.capstone.pickIt.api.chat.dto.request.DirectChatRoomCreateRequestDTO;

public interface ChatRoomCommandService {
    DirectChatRoomResponseDTO.CreateOrEnter createOrEnterDirectChatRoom(
            Long currentUserId,
            DirectChatRoomCreateRequestDTO request
    );
}
