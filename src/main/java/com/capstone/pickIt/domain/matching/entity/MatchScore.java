package com.capstone.pickIt.domain.matching.entity;

import com.capstone.pickIt.domain.course.entity.UserCourseProfile;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.CreatedBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "match_scores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MatchScore extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_course_profile_id", nullable = false)
    private UserCourseProfile userCourseProfile;

    @Column(name = "trait_score", nullable = false)
    private int traitScore;

    @Column(name = "importance_score", nullable = false)
    private int importanceScore;

    @Column(name = "level_score", nullable = false)
    private int levelScore;

    @Column(name = "total_score", nullable = false)
    private int totalScore;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    public static MatchScore of(User user, UserCourseProfile profile,
                                int traitScore, int importanceScore, int levelScore) {
        return MatchScore.builder()
                .user(user)
                .userCourseProfile(profile)
                .traitScore(traitScore)
                .importanceScore(importanceScore)
                .levelScore(levelScore)
                .totalScore(traitScore + importanceScore + levelScore)
                .calculatedAt(LocalDateTime.now())
                .build();
    }
}