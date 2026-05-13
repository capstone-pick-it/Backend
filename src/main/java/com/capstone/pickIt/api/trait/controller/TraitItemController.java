package com.capstone.pickIt.api.trait.controller;

import com.capstone.pickIt.api.trait.dto.response.TraitItemResponseDTO;
import com.capstone.pickIt.api.trait.service.TraitItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/traits")
@RequiredArgsConstructor
public class TraitItemController {

    private final TraitItemService traitItemService;

    @GetMapping
    public ResponseEntity<List<TraitItemResponseDTO>> getTraitItems() {
        return ResponseEntity.ok(traitItemService.getTraitItems());
    }
}