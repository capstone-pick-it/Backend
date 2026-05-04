package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_team_completion_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectTeamCompletionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completion_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_team_id", nullable = false)
    private ProjectTeam projectTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CompletionRequestStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;

    public void approve() {
        this.status = CompletionRequestStatus.APPROVED;
        this.finalizedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = CompletionRequestStatus.REJECTED;
        this.finalizedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = CompletionRequestStatus.CANCELLED;
        this.finalizedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = CompletionRequestStatus.PENDING;
        }

        if (this.requestedAt == null) {
            this.requestedAt = LocalDateTime.now();
        }
    }
}