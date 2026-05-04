package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.CreatedBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "team_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamRequest extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_request_id")
    private Long teamRequestId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "chat_room_id", nullable = false)
//    private ChatRoom chatRoom;

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

    public void accept() {
        this.teamRequestStatus = TeamRequestStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now(ZoneOffset.UTC);
    }
    public void reject() {
        this.teamRequestStatus = TeamRequestStatus.REJECTED;
        this.respondedAt = LocalDateTime.now(ZoneOffset.UTC);
    }
    public void cancel() {
        this.teamRequestStatus = TeamRequestStatus.CANCELED;
        this.respondedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    @PrePersist
    protected void onCreate() {
        this.teamRequestStatus = TeamRequestStatus.PENDING;
    }
}