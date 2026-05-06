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

    @Column(name = "content", nullable = false)
    private String content;
}
