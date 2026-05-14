package com.capstone.pickIt.domain.chat.entity;

import com.capstone.pickIt.domain.project.entity.ProjectTeam;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.CreatedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "chat_room",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_direct_chat_room_user_pair",
                        columnNames = {"chat_type", "direct_user_min_id", "direct_user_max_id"}
                )
        }
)
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

    @Column(name = "direct_user_min_id")
    private Long directUserMinId;

    @Column(name = "direct_user_max_id")
    private Long directUserMaxId;

    // 양방향 매핑
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatPart> chatParts = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void validateChatRoomMetadata() {
        if (chatType == null) {
            throw new IllegalArgumentException("chatType은 필수입니다.");
        }

        if (chatType == ChatType.GROUP &&
                (roomName == null || roomName.isBlank())) {
            throw new IllegalArgumentException("GROUP 채팅은 roomName이 필요합니다.");
        }

        if (chatType == ChatType.GROUP && projectTeam == null) {
            throw new IllegalArgumentException("GROUP 채팅은 projectTeam이 필요합니다.");
        }

        if (chatType == ChatType.GROUP &&
                (directUserMinId != null || directUserMaxId != null)) {
            throw new IllegalArgumentException("GROUP 채팅은 directUserMinId와 directUserMaxId를 가질 수 없습니다.");
        }

        if (chatType == ChatType.DIRECT && projectTeam != null) {
            throw new IllegalArgumentException("DIRECT 채팅은 projectTeam을 가질 수 없습니다.");
        }

        if (chatType == ChatType.DIRECT &&
                (directUserMinId == null || directUserMaxId == null)) {
            throw new IllegalArgumentException("DIRECT 채팅은 directUserMinId와 directUserMaxId가 필요합니다.");
        }
    }

    public static ChatRoom createDirectRoom(User user1, User user2) {
        Long user1Id = user1.getId();
        Long user2Id = user2.getId();

        Long minId = Math.min(user1Id, user2Id);
        Long maxId = Math.max(user1Id, user2Id);

        return ChatRoom.builder()
                .chatType(ChatType.DIRECT)
                .directUserMinId(minId)
                .directUserMaxId(maxId)
                .build();
    }

    public void addParticipant(User user) {
        ChatPart chatPart = ChatPart.create(this, user);
        this.chatParts.add(chatPart);
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
