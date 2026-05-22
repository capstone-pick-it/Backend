package com.capstone.pickIt.domain.course.entity;

import com.capstone.pickIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Column(name = "semester", nullable = false, length = 20)
    private String semester;

    public void update(String courseName, String semester) {
        this.courseName = courseName;
        this.semester = semester;
    }
}