package com.capstone.pickIt.domain.chat.entity;

import com.capstone.pickIt.domain.project.entity.ProjectTeam;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_type", nullable = false)
    private ChatType chatType;

    @Column(name = "room_name", length = 100)
    private String roomName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_team_id", unique = true)
    private ProjectTeam projectTeam;

    @PrePersist
    @PreUpdate
    private void validateChatRoomMetadata() {
        if (chatType == ChatType.GROUP) {
            if (roomName == null || roomName.isBlank()) {
                throw new IllegalStateException("단체 채팅방은 채팅방 이름이 필요합니다.");
            }

            if (projectTeam == null) {
                throw new IllegalStateException("단체 채팅방은 프로젝트 팀 정보가 필요합니다.");
            }
        }

        if (chatType == ChatType.DIRECT) {
            if (projectTeam != null) {
                throw new IllegalStateException("1:1 채팅방은 프로젝트 팀 정보를 가질 수 없습니다.");
            }
        }
    }

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
