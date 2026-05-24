package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.dto.request.ChatMessageSendRequestDTO;

public interface ChatMessageCommandService {

    void sendMessage(Long currentUserId, ChatMessageSendRequestDTO request);
}
