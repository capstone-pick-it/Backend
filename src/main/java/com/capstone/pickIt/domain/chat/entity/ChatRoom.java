package com.capstone.pickIt.domain.chat.entity;

import com.capstone.pickIt.global.entity.CreatedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "chat_room")
public class ChatRoom extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    public void updateLastMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("마지막 메시지는 null일 수 없습니다.");
        }

        if (message.getCreatedAt() == null) {
            throw new IllegalArgumentException("저장되지 않은 메시지는 마지막 메시지로 설정할 수 없습니다.");
        }

        this.lastMessage = message;
        this.lastMessageAt = message.getCreatedAt();
    }
}
