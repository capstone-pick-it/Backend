package com.capstone.pickIt.api.chat.controller;

import com.capstone.pickIt.api.chat.code.ChatSuccessCode;
import com.capstone.pickIt.api.chat.dto.request.DirectChatRoomCreateRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.DirectChatRoomResponseDTO;
import com.capstone.pickIt.api.chat.service.ChatRoomCommandService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatRoomController {

    private final ChatRoomCommandService chatRoomCommandService;

    @PostMapping
    public ApiResponse<DirectChatRoomResponseDTO.CreateOrEnter> createOrEnterDirectChatRoom(
            @RequestBody @Valid DirectChatRoomCreateRequestDTO request
    ) {
        Long currentUserId = SecurityUtil.requireUserId();

        DirectChatRoomResponseDTO.CreateOrEnter result =
                chatRoomCommandService.createOrEnterDirectChatRoom(currentUserId, request);

        return ApiResponse.onSuccess(
                ChatSuccessCode.DIRECT_CHAT_ROOM_CREATED_OR_ENTERED,
                result
        );
    }
}
