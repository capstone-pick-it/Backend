package com.capstone.pickIt.domain.chat.entity;

import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.CreatedDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "chat_part",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chat_part_user_room",
                        columnNames = {"user_id", "chat_room_id"}
                )
        }
)
public class ChatPart extends CreatedDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_read_message_id")
    private Message lastReadMessage;

    public void leave() {
        softDelete();
    }

    public void restore() {
        setDeletedAt(null);
    }

    public void updateLastReadMessage(Message message) {
        this.lastReadMessage = message;
    }

}
