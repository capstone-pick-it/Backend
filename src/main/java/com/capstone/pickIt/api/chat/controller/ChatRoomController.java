package com.capstone.pickIt.api.chat.controller;

import com.capstone.pickIt.api.chat.code.ChatSuccessCode;
import com.capstone.pickIt.api.chat.dto.request.DirectChatRoomCreateRequestDTO;
import com.capstone.pickIt.api.chat.dto.request.TeamRequestCreateRequestDTO;
import com.capstone.pickIt.api.chat.dto.response.ChatRoomResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.CommonCourseResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.DirectChatRoomResponseDTO;
import com.capstone.pickIt.api.chat.dto.response.TeamRequestResponseDTO;
import com.capstone.pickIt.api.chat.service.ChatRoomCommandService;
import com.capstone.pickIt.api.chat.service.ChatRoomQueryService;
import com.capstone.pickIt.global.apiPayload.response.ApiResponse;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Chat", description = "채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatRoomController {

    private final ChatRoomCommandService chatRoomCommandService;
    private final ChatRoomQueryService chatRoomQueryService;

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

    @Operation(
            summary = "채팅방 목록 조회",
            description = "현재 사용자가 참여 중인 채팅방 목록을 최신 메시지 순으로 조회합니다."
    )
    @GetMapping
    public ApiResponse<ChatRoomResponseDTO.ListResponse> getMyChatRooms(
            @RequestParam(required = false) Long cursor
    ) {
        Long currentUserId = SecurityUtil.requireUserId();

        return ApiResponse.onSuccess(
                ChatSuccessCode.CHAT_ROOM_LIST_FOUND,
                chatRoomQueryService.getMyChatRooms(currentUserId, cursor)
        );
    }

    @Operation(
            summary = "팀원 요청 보내기",
            description = "현재 사용자가 1:1 채팅방의 상대 사용자에게 팀원 요청을 보냅니다."
    )
    @PostMapping("/{chatRoomId}/team-requests")
    public ApiResponse<TeamRequestResponseDTO.Create> createTeamRequest(
            @PathVariable Long chatRoomId,
            @RequestBody @Valid TeamRequestCreateRequestDTO request
    ) {
        Long currentUserId = SecurityUtil.requireUserId();

        TeamRequestResponseDTO.Create result =
                chatRoomCommandService.createTeamRequest(currentUserId, chatRoomId, request);

        return ApiResponse.onSuccess(
                ChatSuccessCode.TEAM_REQUEST_CREATED,
                result
        );
    }

    @Operation(
            summary = "공통 과목 목록 조회",
            description = "팀원 요청 전송 전, 현재 사용자와 채팅 상대방 간의 팀원 요청 가능한 공통 과목 목록을 조회합니다."
    )
    @GetMapping("/{chatRoomId}/common-courses")
    public ApiResponse<CommonCourseResponseDTO.CommonCourseList> getCommonCourses(
            @PathVariable Long chatRoomId
    ) {
        Long currentUserId = SecurityUtil.requireUserId();

        CommonCourseResponseDTO.CommonCourseList result =
                chatRoomQueryService.getCommonCourses(currentUserId, chatRoomId);

        return ApiResponse.onSuccess(
                ChatSuccessCode.COMMON_COURSE_LIST_FETCHED,
                result
        );
    }

    @Operation(
            summary = "팀원 요청 수락",
            description = "현재 사용자가 받은 PENDING 상태의 팀원 요청을 수락합니다."
    )
    @PatchMapping("/{chatRoomId}/team-requests/{teamRequestId}")
    public ApiResponse<TeamRequestResponseDTO.Respond> acceptTeamRequest(
            @PathVariable Long chatRoomId,
            @PathVariable Long teamRequestId
    ) {
        Long currentUserId = SecurityUtil.requireUserId();

        TeamRequestResponseDTO.Respond result =
                chatRoomCommandService.acceptTeamRequest(currentUserId, chatRoomId, teamRequestId);

        return ApiResponse.onSuccess(
                ChatSuccessCode.TEAM_REQUEST_RESPONDED,
                result
        );
    }
}
