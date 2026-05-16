package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "project_team_completion_approval",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_completion_approval_request_user",
                        columnNames = {"completion_request_id", "user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectTeamCompletionApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completion_approval_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completion_request_id", nullable = false)
    private ProjectTeamCompletionRequest completionRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 20)
    private CompletionDecision decision;

    @Column(name = "decided_at", nullable = false)
    private LocalDateTime decidedAt;

    public void changeDecision(CompletionDecision decision) {
        this.decision = decision;
        this.decidedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.decidedAt == null) {
            this.decidedAt = LocalDateTime.now();
        }
    }
}