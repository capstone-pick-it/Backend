package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.ChecklistItem;
import com.capstone.pickIt.domain.project.entity.ProjectTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {

    Optional<ChecklistItem> findByIdAndDeletedAtIsNull(Long id);

    @Query("""
        SELECT c
        FROM ChecklistItem c
        JOIN FETCH c.manager
        JOIN FETCH c.createdByUser
        LEFT JOIN FETCH c.completedByUser
        JOIN FETCH c.projectTeam
        WHERE c.projectTeam = :projectTeam
          AND c.deletedAt IS NULL
        ORDER BY
          CASE WHEN c.status = com.capstone.pickIt.domain.project.entity.ChecklistStatus.TODO THEN 0 ELSE 1 END,
          c.dueDate ASC,
          c.createdAt ASC
    """)
    List<ChecklistItem> findActiveItemsOrderByTodoFirstDueDateAscCreatedAtAsc(
            @Param("projectTeam") ProjectTeam projectTeam
    );
}