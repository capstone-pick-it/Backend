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
        finalizeAs(CompletionRequestStatus.APPROVED);
    }

    public void reject() {
        finalizeAs(CompletionRequestStatus.REJECTED);
    }

    public void cancel() {
        finalizeAs(CompletionRequestStatus.CANCELLED);
    }

    private void finalizeAs(CompletionRequestStatus nextStatus) {
        if (this.finalizedAt != null) {
            throw new IllegalStateException("이미 종료된 요청입니다.");
        }
        this.status = nextStatus;
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