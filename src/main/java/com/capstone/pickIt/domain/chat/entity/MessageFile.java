package com.capstone.pickIt.domain.chat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "message_file")
public class MessageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    /**
     * 현재 컬럼명은 file_url이지만, 비공개 GCS 버킷 접근을 위해
     * 실제 공개 URL이 아니라 GCS objectName을 저장함
     * (예: chat/{userId}/{uuid}.png)
     */
    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "content_type", nullable = false, length = 150)
    private String contentType;

    @PrePersist
    @PreUpdate
    private void validateFileMetadata() {
        if (fileSize == null || fileSize < 0) {
            throw new IllegalArgumentException("fileSize는 0 이상이어야 합니다.");
        }
    }

    public static MessageFile create(
            Message message,
            String fileUrl,
            String fileName,
            Long fileSize,
            String contentType
    ) {
        return MessageFile.builder()
                .message(message)
                .fileUrl(fileUrl)
                .fileName(fileName)
                .fileSize(fileSize)
                .contentType(contentType)
                .build();
    }
}