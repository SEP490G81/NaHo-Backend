package org.naho.topic.repository;

import org.naho.topic.entity.TopicEntity;
import org.naho.user.type.JLPTLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface TopicJpaRepository extends JpaRepository<TopicEntity, Long> {
    boolean existsByJapaneseNameAndJlptLevel(String japaneseName, JLPTLevel jlptLevel);

    boolean existsByJapaneseNameAndJlptLevelAndIdNot(String japaneseName, JLPTLevel jlptLevel, Long id);

    @Query("SELECT MAX(t.orderIndex) FROM TopicEntity t")
    Double getMaxOrderIndex();

    @Query("SELECT t FROM TopicEntity t JOIN t.lessons l JOIN l.objectives o WHERE o.id = :objectiveId")
    Optional<TopicEntity> findByObjectiveId(@Param("objectiveId") Long objectiveId);
}
