package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.CreatedBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "team_request",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pending_team_request_sender_course",
                        columnNames = {"sender_id", "course_id", "pending_unique_flag"}
                ),
                @UniqueConstraint(
                        name = "uk_pending_team_request_sender_receiver",
                        columnNames = {"sender_id", "receiver_id", "pending_unique_flag"}
                )
        }
)
public class TeamRequest extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "team_request_status", nullable = false)
    private TeamRequestStatus teamRequestStatus;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "pending_unique_flag")
    private Boolean pendingUniqueFlag;

    public void accept() {
        this.teamRequestStatus = TeamRequestStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now(ZoneOffset.UTC);
        this.pendingUniqueFlag = null;
    }

    public void reject() {
        this.teamRequestStatus = TeamRequestStatus.REJECTED;
        this.respondedAt = LocalDateTime.now(ZoneOffset.UTC);
        this.pendingUniqueFlag = null;
    }

    public void cancel() {
        this.teamRequestStatus = TeamRequestStatus.CANCELED;
        this.respondedAt = LocalDateTime.now(ZoneOffset.UTC);
        this.pendingUniqueFlag = null;
    }

    @PrePersist
    protected void onCreate() {
        if (this.teamRequestStatus == null) {
            this.teamRequestStatus = TeamRequestStatus.PENDING;
        }

        if (this.teamRequestStatus == TeamRequestStatus.PENDING) {
            this.pendingUniqueFlag = true;
        }
    }

    public static TeamRequest create(
            ChatRoom chatRoom,
            Course course,
            User sender,
            User receiver
    ) {
        return TeamRequest.builder()
                .chatRoom(chatRoom)
                .course(course)
                .sender(sender)
                .receiver(receiver)
                .teamRequestStatus(TeamRequestStatus.PENDING)
                .pendingUniqueFlag(true)
                .build();
    }

}