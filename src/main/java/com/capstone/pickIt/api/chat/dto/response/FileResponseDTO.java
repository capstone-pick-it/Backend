package com.capstone.pickIt.api.chat.dto.response;

import java.util.List;

public class FileResponseDTO {

    public record UploadResponse(
            List<FileInfo> files
    ) {
    }

    public record FileInfo(
            String fileName,
            String fileUrl,
            Long fileSize,
            String contentType
    ) {
    }
}
