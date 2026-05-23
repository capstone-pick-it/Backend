package com.capstone.pickIt.domain.teamlevel.entity;

import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "team_level",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_team_level_user", columnNames = "user_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamLevel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Column(name = "level", nullable = false)
    private int level = 0;

    public void levelUp() {
        this.level++;
    }
}