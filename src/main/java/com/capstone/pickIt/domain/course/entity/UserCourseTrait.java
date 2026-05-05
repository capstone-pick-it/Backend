package com.capstone.pickIt.domain.course.entity;

import com.capstone.pickIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_course_traits",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_course_traits_profile_trait",
                        columnNames = {"user_course_profile_id", "trait_items_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserCourseTrait extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_course_traits_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_course_profile_id", nullable = false)
    private UserCourseProfile userCourseProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trait_items_id", nullable = false)
    private TraitItem traitItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "selected_side", nullable = false, length = 1)
    private TraitSide selectedSide;
}