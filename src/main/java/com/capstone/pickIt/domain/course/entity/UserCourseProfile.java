package com.capstone.pickIt.domain.course.entity;

import com.capstone.pickIt.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_course_profile",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_course_profile_user_course",
                        columnNames = {"user_id", "course_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserCourseProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_course_profile_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "importance_level", nullable = false, length = 20)
    private ImportanceLevel importanceLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "recruitment_status", nullable = false, length = 30)
    private RecruitmentStatus recruitmentStatus;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void changeRecruitmentStatus(RecruitmentStatus recruitmentStatus) {
        if (recruitmentStatus == null) {
            throw new IllegalArgumentException("모집 상태는 필수입니다.");
        }

        this.recruitmentStatus = recruitmentStatus;
    }

    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.importanceLevel == null) {
            this.importanceLevel = ImportanceLevel.HIGH;
        }

        if (this.recruitmentStatus == null) {
            this.recruitmentStatus = RecruitmentStatus.RECRUITING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}