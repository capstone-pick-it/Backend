package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.domain.user.entity.User;
//import com.capstone.pickIt.domain.chat.entity.ChatRoom;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamRequest {

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

    @Column(name = "team_request_status", nullable = false)
    private String teamRequestStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    public void accept() {
        this.teamRequestStatus = "ACCEPTED";
        this.respondedAt = LocalDateTime.now();
    }

    public void reject() {
        this.teamRequestStatus = "REJECTED";
        this.respondedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.teamRequestStatus = "CANCELED";
        this.respondedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.teamRequestStatus = "PENDING";
    }
}