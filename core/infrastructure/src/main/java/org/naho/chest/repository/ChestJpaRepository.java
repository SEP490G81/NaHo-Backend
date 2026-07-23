package org.naho.chest.repository;

import org.naho.chest.entity.ChestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChestJpaRepository extends JpaRepository<ChestEntity, Long> {
    List<ChestEntity> findAllByIdIn(Collection<Long> ids);
}
