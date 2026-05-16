package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.ChecklistItem;
import com.capstone.pickIt.domain.project.entity.ProjectTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {

    List<ChecklistItem> findAllByProjectTeamAndDeletedAtIsNullOrderByIdAsc(ProjectTeam projectTeam);

    Optional<ChecklistItem> findByIdAndDeletedAtIsNull(Long id);
}