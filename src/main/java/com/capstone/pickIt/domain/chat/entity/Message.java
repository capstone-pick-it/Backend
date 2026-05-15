package com.capstone.pickIt.domain.chat.entity;

import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.CreatedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "message",
        indexes = {
                @Index(
                        name = "idx_message_chatroom_message",
                        columnList = "chat_room_id, message_id"
                )
        }
)
public class Message extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @PrePersist
    @PreUpdate
    private void validateMessage() {
        if (messageType == null) {
            throw new IllegalArgumentException("messageType은 필수입니다.");
        }

        if (messageType == MessageType.TEXT &&
                (content == null || content.isBlank())) {
                throw new IllegalArgumentException("TEXT 메시지는 content가 필요합니다.");
        }

        if (messageType == MessageType.FILE &&
                (content != null && !content.isBlank())) {
            throw new IllegalArgumentException("FILE 메시지는 content를 가질 수 없습니다.");
        }
    }
}
