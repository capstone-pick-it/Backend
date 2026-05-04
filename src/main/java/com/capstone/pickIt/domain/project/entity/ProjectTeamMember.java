package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "project_team_member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_team_member_team_user",
                        columnNames = {"project_team_id", "user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectTeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_team_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_team_id", nullable = false)
    private ProjectTeam projectTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private ProjectTeamMemberRole role;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "review_completed_at")
    private LocalDateTime reviewCompletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "recruitment_confirm_status", nullable = false, length = 20)
    private RecruitmentConfirmStatus recruitmentConfirmStatus;

    public void confirm() {
        this.recruitmentConfirmStatus = RecruitmentConfirmStatus.CONFIRMED;
    }

    public void reject() {
        this.recruitmentConfirmStatus = RecruitmentConfirmStatus.REJECTED;
    }

    public void leave() {
        this.leftAt = LocalDateTime.now();
    }

    public void completeReview() {
        this.reviewCompletedAt = LocalDateTime.now();
    }

    public boolean isActiveMember() {
        return this.leftAt == null;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;

        if (this.joinedAt == null) {
            this.joinedAt = now;
        }

        if (this.role == null) {
            this.role = ProjectTeamMemberRole.MEMBER;
        }

        if (this.recruitmentConfirmStatus == null) {
            this.recruitmentConfirmStatus = RecruitmentConfirmStatus.PENDING;
        }
    }
}