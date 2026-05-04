package com.capstone.pickIt.domain.project.repository;

import com.capstone.pickIt.domain.project.entity.ProjectTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTeamMemberRepository extends JpaRepository<ProjectTeamMember, Long> {

    List<ProjectTeamMember> findByProjectTeam_Id(Long projectTeamId);
}