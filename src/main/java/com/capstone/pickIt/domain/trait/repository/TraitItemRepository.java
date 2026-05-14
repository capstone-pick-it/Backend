package com.capstone.pickIt.domain.trait.repository;

import com.capstone.pickIt.domain.trait.entity.TraitItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraitItemRepository extends JpaRepository<TraitItem, Long> {

}