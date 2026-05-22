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