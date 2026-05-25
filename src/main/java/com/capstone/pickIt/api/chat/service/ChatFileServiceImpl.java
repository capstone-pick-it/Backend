package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.dto.response.FileResponseDTO;
import com.capstone.pickIt.domain.chat.exception.ChatErrorCode;
import com.capstone.pickIt.domain.chat.exception.ChatException;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatFileServiceImpl implements ChatFileService {

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final int MAX_FILE_COUNT = 5;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            // images
            "image/png",
            "image/jpeg",
            "image/webp",
            "image/gif",

            // documents
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain"
    );

    @Value("${gcp.storage.bucket}")
    private String bucketName;

    private final Storage storage;

    @Override
    public FileResponseDTO.UploadResponse uploadFiles(
            Long currentUserId,
            List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            throw new ChatException(ChatErrorCode.MESSAGE_FILE_REQUIRED);
        }

        if (files.size() > MAX_FILE_COUNT) {
            throw new ChatException(ChatErrorCode.FILE_COUNT_EXCEEDED);
        }

        List<String> uploadedObjectNames = new ArrayList<>();

        try {
            List<FileResponseDTO.FileInfo> uploadedFiles = files.stream()
                    .map(file -> uploadSingleFile(currentUserId, file, uploadedObjectNames))
                    .toList();

            return new FileResponseDTO.UploadResponse(uploadedFiles);

        } catch (ChatException e) {
            rollbackUploadedFiles(uploadedObjectNames);
            throw e;

        } catch (Exception e) {
            log.error("GCS 파일 업로드 실패", e);
            rollbackUploadedFiles(uploadedObjectNames);
            throw new ChatException(ChatErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private FileResponseDTO.FileInfo uploadSingleFile(
            Long currentUserId,
            MultipartFile file,
            List<String> uploadedObjectNames
    ) {
        validateFile(file);

        String originalFileName = file.getOriginalFilename();
        String extension = getExtension(originalFileName);
        String storedFileName = "chat/" + currentUserId + "/" + UUID.randomUUID() + extension;

        try {
            BlobInfo blobInfo = BlobInfo.newBuilder(
                            bucketName,
                            storedFileName
                    )
                    .setContentType(file.getContentType())
                    .build();

            storage.create(
                    blobInfo,
                    file.getBytes(),
                    Storage.BlobTargetOption.doesNotExist()
            );

            uploadedObjectNames.add(storedFileName);

            return new FileResponseDTO.FileInfo(
                    originalFileName,
                    getFileUrl(storedFileName),
                    file.getSize(),
                    file.getContentType()
            );

        } catch (IOException e) {
            throw new ChatException(ChatErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ChatException(ChatErrorCode.MESSAGE_FILE_REQUIRED);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ChatException(ChatErrorCode.FILE_SIZE_EXCEEDED);
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new ChatException(ChatErrorCode.INVALID_FILE_TYPE);
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String getFileUrl(String storedFileName) {
        return "https://storage.googleapis.com/"
                + bucketName
                + "/"
                + storedFileName;
    }

    private void rollbackUploadedFiles(List<String> uploadedObjectNames) {
        uploadedObjectNames.forEach(objectName -> {
            try {
                storage.delete(bucketName, objectName);
            } catch (Exception cleanupEx) {
                log.warn("업로드 롤백 삭제 실패: objectName={}", objectName, cleanupEx);
            }
        });
    }
}
