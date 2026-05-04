package com.capstone.pickIt.domain.point.entity;

import com.capstone.pickIt.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "point",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_point_user", columnNames = "user_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "balance", nullable = false)
    private Integer balance;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void add(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("추가 포인트는 1 이상이어야 합니다.");
        }
        if (this.balance == null) {
            this.balance = 0;
        }

        this.balance += amount;
    }

    public void subtract(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("추가 포인트는 1 이상이어야 합니다.");
        }
        if (this.balance == null) {
            this.balance = 0;
        }

        // 포인트는 음수로 내려갈 수 있으므로 this.balance < amount는 제한하지 않음
        this.balance -= amount;
    }

    @PrePersist
    protected void onCreate() {
        if (this.balance == null) {
            this.balance = 0;
        }

        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}