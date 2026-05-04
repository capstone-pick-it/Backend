package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.ProjectTeam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTeamRepository extends JpaRepository<ProjectTeam, Long> {
}