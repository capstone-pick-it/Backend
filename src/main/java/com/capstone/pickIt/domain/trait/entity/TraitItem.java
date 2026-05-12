package com.capstone.pickIt.domain.trait.entity;

import com.capstone.pickIt.global.entity.CreatedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trait_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TraitItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trait_items_id")
    private Long traitItemsId;

    @Column(name = "name_a", nullable = false)
    private String nameA;

    @Column(name = "name_b", nullable = false)
    private String nameB;

    @Column(name = "complementary_allowed", nullable = false)
    private boolean complementaryAllowed;
}