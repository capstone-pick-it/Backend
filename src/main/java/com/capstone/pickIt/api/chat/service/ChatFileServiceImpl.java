package com.capstone.pickIt.api.chat.service;

import com.capstone.pickIt.api.chat.dto.response.FileResponseDTO;
import com.capstone.pickIt.domain.chat.exception.ChatErrorCode;
import com.capstone.pickIt.domain.chat.exception.ChatException;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatFileServiceImpl implements ChatFileService {

    @Value("${gcp.storage.bucket}")
    private String bucketName;

    private final Storage storage;

    @Override
    public FileResponseDTO.UploadResponse uploadFiles(
            List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            throw new ChatException(ChatErrorCode.MESSAGE_FILE_REQUIRED);
        }

        List<String> uploadedObjectNames = new ArrayList<>();

        try {
            List<FileResponseDTO.FileInfo> uploadedFiles = files.stream()
                    .map(file -> uploadSingleFile(file, uploadedObjectNames))
                    .toList();

            return new FileResponseDTO.UploadResponse(uploadedFiles);

        } catch (Exception e) {
            uploadedObjectNames.forEach(objectName -> {
                try {
                    storage.delete(bucketName, objectName);
                } catch (Exception ignored) {}
            });

            throw new ChatException(ChatErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private FileResponseDTO.FileInfo uploadSingleFile(
            MultipartFile file,
            List<String> uploadedObjectNames
    ) {
        if (file.isEmpty()) {
            throw new ChatException(ChatErrorCode.MESSAGE_FILE_REQUIRED);
        }

        String originalFileName = file.getOriginalFilename();
        String extension = getExtension(originalFileName);
        String storedFileName = "chat/" + UUID.randomUUID() + extension;

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
                    file.getContentType(),
                    file.getSize()
            );

        } catch (IOException e) {
            throw new ChatException(ChatErrorCode.FILE_UPLOAD_FAILED);
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
}
