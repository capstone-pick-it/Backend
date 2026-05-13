package com.capstone.pickIt.api.user.controller;

import com.capstone.pickIt.api.user.dto.request.UserDefaultTraitRequestDTO;
import com.capstone.pickIt.api.user.dto.response.UserDefaultTraitResponseDTO;
import com.capstone.pickIt.api.user.service.UserDefaultTraitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserDefaultTraitController {

    private final UserDefaultTraitService userDefaultTraitService;

    @GetMapping("/{userId}/traits/default")
    public ResponseEntity<List<UserDefaultTraitResponseDTO>> getDefaultTraits(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userDefaultTraitService.getDefaultTraits(userId));
    }

    @PutMapping("/{userId}/traits/default")
    public ResponseEntity<List<UserDefaultTraitResponseDTO>> updateDefaultTraits(
            @PathVariable Long userId,
            @RequestBody List<UserDefaultTraitRequestDTO> requests) {
        userDefaultTraitService.updateDefaultTraits(userId, requests);
        return ResponseEntity.ok(userDefaultTraitService.getDefaultTraits(userId));
    }
}