package com.capstone.pickIt.domain.user.entity;

import com.capstone.pickIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    //기본 정보 엔티티 추가
    @Column(name = "school", length = 100)
    private String school;

    @Column(name = "major", length = 100)
    private String major;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "semester", length = 20)
    private String semester;

    //온보딩 엔티티 추가
    @Builder.Default
    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted = false;

    @Builder.Default
    @Column(name = "onboarding_step", nullable = false)
    private int onboardingStep = 1;

    @Version
    private Long version;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;

    public void updateBasicInfo(String school, String major, Integer grade, String semester) {
        this.school = school;
        this.major = major;
        this.grade = grade;
        this.semester = semester;
        this.onboardingStep = 2;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void withdraw() {
        this.deletedAt = java.time.LocalDateTime.now();
    }

    public boolean isWithdrawn() {
        return this.deletedAt != null;
    }
}