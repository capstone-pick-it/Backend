package com.capstone.pickIt.api.chat.controller;

import com.capstone.pickIt.api.chat.dto.request.ChatMessageSendRequestDTO;
import com.capstone.pickIt.api.chat.service.ChatMessageCommandService;
import com.capstone.pickIt.global.apiPayload.exception.CustomException;
import com.capstone.pickIt.global.apiPayload.response.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageSocketController {

    private final ChatMessageCommandService chatMessageCommandService;

    @MessageMapping("/chat.send")
    public void sendMessage(
            @Valid ChatMessageSendRequestDTO request,
            StompHeaderAccessor accessor
    ) {
        Long currentUserId = (Long) accessor.getSessionAttributes().get("userId");

        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        chatMessageCommandService.sendMessage(
                currentUserId,
                request
        );
    }
}
