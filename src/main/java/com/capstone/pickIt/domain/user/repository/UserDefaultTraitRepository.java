package com.capstone.pickIt.domain.user.repository;

import com.capstone.pickIt.domain.user.entity.UserDefaultTrait;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserDefaultTraitRepository extends JpaRepository<UserDefaultTrait, Long> {

    List<UserDefaultTrait> findByUserId(Long id);

    void deleteByUserId(Long id);
}