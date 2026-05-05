package com.capstone.pickIt.domain.course.entity;

import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.CreatedUpdatedDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

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
public class UserCourseProfile extends CreatedUpdatedDeletedBaseEntity {

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

    public void changeRecruitmentStatus(RecruitmentStatus recruitmentStatus) {
        if (recruitmentStatus == null) {
            throw new IllegalArgumentException("모집 상태는 필수입니다.");
        }

        this.recruitmentStatus = recruitmentStatus;
    }

    @PrePersist
    protected void onCreateUserCourseProfile() {
        if (this.importanceLevel == null) {
            this.importanceLevel = ImportanceLevel.HIGH;
        }

        if (this.recruitmentStatus == null) {
            this.recruitmentStatus = RecruitmentStatus.RECRUITING;
        }
    }
}