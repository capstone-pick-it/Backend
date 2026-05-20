package com.capstone.pickIt.api.trait.controller;

import com.capstone.pickIt.api.trait.dto.response.TraitItemResponseDTO;
import com.capstone.pickIt.api.trait.service.TraitItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Trait", description = "성향 항목 API")
@RestController
@RequestMapping("/api/traits")
@RequiredArgsConstructor
public class TraitItemController {

    private final TraitItemService traitItemService;

    @Operation(summary = "성향 항목 전체 조회", description = "서비스에서 제공하는 모든 성향 항목 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<TraitItemResponseDTO>> getTraitItems() {
        return ResponseEntity.ok(traitItemService.getTraitItems());
    }
}