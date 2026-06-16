package org.naho.topic.repository;

import org.naho.topic.entity.TopicEntity;
import org.naho.user.type.JLPTLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicJpaRepository extends JpaRepository<TopicEntity, Long> {
    boolean existsByNameAndJlptLevel(String name, JLPTLevel jlptLevel);

    boolean existsByNameAndJlptLevelAndIdNot(String name, JLPTLevel jlptLevel, Long id);

    @Query("SELECT MAX(t.orderIndex) FROM TopicEntity t")
    Double getMaxOrderIndex();
}
