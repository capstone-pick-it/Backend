package com.capstone.pickIt.api.chat.controller;

import com.capstone.pickIt.api.chat.code.ChatSuccessCode;
import com.capstone.pickIt.api.chat.dto.request.DirectChatRoomCreateRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.DirectChatRoomResponseDTO;
import com.capstone.pickIt.api.chat.service.ChatRoomCommandService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Chat", description = "채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatRoomController {

    private final ChatRoomCommandService chatRoomCommandService;

    @Operation(summary = "1:1 채팅방 생성/재입장", description = "상대 사용자와의 1:1 채팅방을 생성하거나, 기존 채팅방이 존재하면 재사용 및 재입장 처리합니다.")
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
