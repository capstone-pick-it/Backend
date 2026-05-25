package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.CreatedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_leave_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TeamLeaveRequest extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_leave_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_team_id", nullable = false)
    private ProjectTeam projectTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TeamLeaveRequestStatus status;

    public void approve() {
        this.status = TeamLeaveRequestStatus.APPROVED;
    }

    public static TeamLeaveRequest create(ProjectTeam projectTeam, User requester) {
        return TeamLeaveRequest.builder()
                .projectTeam(projectTeam)
                .requester(requester)
                .status(TeamLeaveRequestStatus.PENDING)
                .build();
    }
}
