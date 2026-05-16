package com.capstone.pickIt.api.trait.service;

import com.capstone.pickIt.api.trait.dto.response.TraitItemResponseDTO;
import com.capstone.pickIt.domain.trait.repository.TraitItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TraitItemService {

    private final TraitItemRepository traitItemRepository;

    public List<TraitItemResponseDTO> getTraitItems() {
        return traitItemRepository.findAll()
                .stream()
                .map(TraitItemResponseDTO::from)
                .toList();
    }
}
