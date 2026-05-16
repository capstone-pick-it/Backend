package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "peer_review",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_peer_review_team_reviewer_reviewee",
                        columnNames = {"project_team_id", "reviewer_user_id", "reviewee_user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PeerReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "peer_review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_team_id", nullable = false)
    private ProjectTeam projectTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_user_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_user_id", nullable = false)
    private User reviewee;

    @Column(name = "completion_score", nullable = false)
    private Integer completionScore;

    @Column(name = "proactivity_score", nullable = false)
    private Integer proactivityScore;

    @Column(name = "satisfaction_score", nullable = false)
    private Integer satisfactionScore;

    @Column(name = "average_score", nullable = false, precision = 4, scale = 2)
    private BigDecimal averageScore;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreatePeerReview() {
        validateScores();
        calculateAverageScore();

        LocalDateTime now = LocalDateTime.now();

        if (this.submittedAt == null) {
            this.submittedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        validateScores();
        calculateAverageScore();
    }

    private void validateScores() {
        validateScore(completionScore);
        validateScore(proactivityScore);
        validateScore(satisfactionScore);
    }

    private void validateScore(Integer score) {
        if (score == null || score < 1 || score > 5) {
            throw new IllegalArgumentException("평가 점수는 1점 이상 5점 이하여야 합니다.");
        }
    }

    private void calculateAverageScore() {
        this.averageScore = BigDecimal.valueOf(
                completionScore + proactivityScore + satisfactionScore
        ).divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
    }
}