package org.naho.learning.repository;

import org.naho.learning.entity.LearningPathNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningPathNodeJpaRepository extends JpaRepository<LearningPathNodeEntity, Long> {
}
