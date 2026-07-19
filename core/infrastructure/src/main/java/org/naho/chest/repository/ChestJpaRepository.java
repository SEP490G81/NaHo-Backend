package org.naho.chest.repository;

import org.naho.chest.entity.ChestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChestJpaRepository extends JpaRepository<ChestEntity, Long> {
}
