package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.CreatedBaseEntity;
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
public class ProjectTeamMember extends CreatedBaseEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "recruitment_confirm_status", nullable = false, length = 20)
    private RecruitmentConfirmStatus recruitmentConfirmStatus;

    public void confirm() {
        if (this.recruitmentConfirmStatus != RecruitmentConfirmStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태에서만 승인할 수 있습니다. 현재 상태: " + this.recruitmentConfirmStatus);
        }
        this.joinedAt = LocalDateTime.now();

        this.recruitmentConfirmStatus = RecruitmentConfirmStatus.CONFIRMED;
    }

    public void reject() {
        if (this.recruitmentConfirmStatus != RecruitmentConfirmStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태에서만 거절할 수 있습니다. 현재 상태: " + this.recruitmentConfirmStatus);
        }

        this.recruitmentConfirmStatus = RecruitmentConfirmStatus.REJECTED;
    }

    public void leave() {
        if (this.leftAt != null) {
            throw new IllegalStateException("이미 탈퇴한 멤버입니다.");
        }

        this.leftAt = LocalDateTime.now();
    }

    public void completeReview() {
        if (this.reviewCompletedAt != null) {
            throw new IllegalStateException("이미 리뷰가 완료된 멤버입니다.");
        }

        this.reviewCompletedAt = LocalDateTime.now();
    }

    public boolean isActiveMember() {
        return this.leftAt == null
                && this.recruitmentConfirmStatus == RecruitmentConfirmStatus.CONFIRMED;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

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