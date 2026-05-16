package com.capstone.pickIt.api.point.dto.response;

import java.util.List;

public record PointTransactionListResponseDTO(
        List<PointTransactionResponseDTO> content,
        Integer page,
        Integer size,
        Long totalElements,
        Boolean hasNext
) {
}