package com.capstone.pickIt.api.user.controller;

import com.capstone.pickIt.api.user.dto.request.UserDefaultTraitRequestDTO;
import com.capstone.pickIt.api.user.dto.response.UserDefaultTraitResponseDTO;
import com.capstone.pickIt.api.user.service.UserDefaultTraitService;
import com.capstone.pickIt.global.config.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserDefaultTraitController {

    private final UserDefaultTraitService userDefaultTraitService;

    // 기본 팀플 성향 조회
    @GetMapping("/me/traits/default")
    public ResponseEntity<List<UserDefaultTraitResponseDTO>> getDefaultTraits() {
        Long userId = SecurityUtil.requireUserId();
        return ResponseEntity.ok(userDefaultTraitService.getDefaultTraits(userId));
    }

    // 기본 팀플 성향 등록/수정
    @PutMapping("/me/traits/default")
    public ResponseEntity<List<UserDefaultTraitResponseDTO>> updateDefaultTraits(
            @RequestBody @Valid List<UserDefaultTraitRequestDTO> requests) {
        Long userId = SecurityUtil.requireUserId();
        return ResponseEntity.ok(userDefaultTraitService.updateDefaultTraits(userId, requests));
    }
}