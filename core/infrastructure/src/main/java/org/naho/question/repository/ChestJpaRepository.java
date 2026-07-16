package org.naho.question.repository;

import org.naho.question.entity.ChestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChestJpaRepository extends JpaRepository<ChestEntity, Long> {
}
