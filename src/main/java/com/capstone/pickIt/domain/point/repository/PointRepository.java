package com.capstone.pickIt.domain.point.repository;

import com.capstone.pickIt.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByUserId(Long userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Point p WHERE p.user.id IN :userIds")
    void deleteAllByUserIdIn(@Param("userIds") List<Long> userIds);
}