package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_team")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectTeam extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_team_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProjectTeamStatus status;

    @Column(name = "progress_rate", precision = 5, scale = 2)
    private BigDecimal progressRate;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    public void start() {
        if (this.status != ProjectTeamStatus.RECRUITING) {
            throw new IllegalStateException("Project team은 RECRUITING 상태에서만 시작 가능합니다.");
        }

        this.status = ProjectTeamStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void complete() {
        if (this.status != ProjectTeamStatus.IN_PROGRESS) {
            throw new IllegalStateException("Project team은 IN_PROGRESS 상태에서만 종료 가능합니다.");
        }

        this.status = ProjectTeamStatus.DONE;
        this.endedAt = LocalDateTime.now();
        this.progressRate = BigDecimal.valueOf(100);
    }

    @PrePersist
    protected void onCreateProjectTeam() {
        if (this.status == null) {
            this.status = ProjectTeamStatus.RECRUITING;
        }
    }
}