package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.dto.response.FileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatFileService {

    FileResponseDTO.UploadResponse uploadFiles(
            Long currentUserId,
            List<MultipartFile> files
    );
}
