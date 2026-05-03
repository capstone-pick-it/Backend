package com.capstone.pickIt.domain.user.entity;

import com.capstone.pickIt.domain.course.entity.TraitItem;
import com.capstone.pickIt.domain.course.entity.TraitSide;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_default_traits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserDefaultTrait {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "default_trait_id")
    private Long defaultTraitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trait_items_id", nullable = false)
    private TraitItem traitItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "selected_side", nullable = false)
    private TraitSide selectedSide;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateSide(TraitSide selectedSide) {
        this.selectedSide = selectedSide;
        this.updatedAt = LocalDateTime.now();
    }
}