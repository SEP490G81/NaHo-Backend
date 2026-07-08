package org.naho.topic.repository;

import org.naho.topic.entity.ObjectiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectiveJpaRepository extends JpaRepository<ObjectiveEntity, Long> {
}
