package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "checklist_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_team_id", nullable = false)
    private ProjectTeam projectTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ChecklistStatus status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by_user_id")
    private User completedByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void update(String title, LocalDate dueDate, User manager) {
        if (title == null || manager == null) {
            throw new IllegalArgumentException("제목과 담당자는 필수입니다.");
        }

        this.title = title;
        this.dueDate = dueDate;
        this.manager = manager;
    }

    public void markDone(User completedByUser) {
        this.status = ChecklistStatus.DONE;
        this.completedByUser = completedByUser;
        this.completedAt = LocalDateTime.now();
    }

    public void markTodo() {
        this.status = ChecklistStatus.TODO;
        this.completedByUser = null;
        this.completedAt = null;
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

        if (this.status == null) {
            this.status = ChecklistStatus.TODO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}