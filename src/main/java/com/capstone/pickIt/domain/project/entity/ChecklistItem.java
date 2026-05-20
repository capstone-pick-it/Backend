package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.CreatedUpdatedDeletedBaseEntity;
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
public class ChecklistItem extends CreatedUpdatedDeletedBaseEntity {

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

    public void updateContent(String title, LocalDate dueDate) {
        if (title != null) {
            if (title.isBlank()) {
                throw new IllegalArgumentException("제목은 공백일 수 없습니다.");
            }
            this.title = title;
        }

        if (dueDate != null) {
            this.dueDate = dueDate;
        }
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

    @PrePersist
    protected void onCreateChecklistItem() {
        if (this.status == null) {
            this.status = ChecklistStatus.TODO;
        }
    }
}