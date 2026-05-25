package com.capstone.pickIt.domain.project.entity;

import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.CreatedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "team_leave_approval",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_team_leave_approval_request_approver",
                        columnNames = {"team_leave_request_id", "approver_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TeamLeaveApproval extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_leave_approval_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_leave_request_id", nullable = false)
    private TeamLeaveRequest teamLeaveRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    public static TeamLeaveApproval create(TeamLeaveRequest teamLeaveRequest, User approver) {
        return TeamLeaveApproval.builder()
                .teamLeaveRequest(teamLeaveRequest)
                .approver(approver)
                .build();
    }
}
