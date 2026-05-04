package com.capstone.pickIt.domain.course.entity;

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
    private Long id;

    @Column(name = "name_a", nullable = false, length = 50)
    private String nameA;

    @Column(name = "name_b", nullable = false, length = 50)
    private String nameB;

    @Column(name = "complementary_allowed", nullable = false)
    private boolean complementaryAllowed;
}