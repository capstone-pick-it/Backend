package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "project_team_review_status",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_team_review_status_team_user",
                        columnNames = {"project_team_id", "user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectTeamReviewStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_status_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_team_id", nullable = false)
    private ProjectTeam projectTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expected_review_count", nullable = false)
    private Integer expectedReviewCount;

    @Column(name = "submitted_review_count", nullable = false)
    private Integer submittedReviewCount;

    @Column(name = "is_completed", nullable = false)
    private boolean completed;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public void increaseSubmittedCount() {
        this.submittedReviewCount += 1;

        if (this.submittedReviewCount >= this.expectedReviewCount) {
            this.completed = true;
            this.completedAt = LocalDateTime.now();
        }
    }

    @PrePersist
    protected void onCreate() {
        if (this.submittedReviewCount == null) {
            this.submittedReviewCount = 0;
        }

        if (this.completed) {
            this.completedAt = LocalDateTime.now();
        }
    }
}