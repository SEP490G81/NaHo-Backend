package org.naho.speech.topic.repository;

import org.naho.speech.topic.entity.TopicEntity;
import org.naho.user.type.JLPTLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicJpaRepository extends JpaRepository<TopicEntity, Long> {
    boolean existsByJapaneseNameAndJlptLevel(String japaneseName, JLPTLevel jlptLevel);

    boolean existsByJapaneseNameAndJlptLevelAndIdNot(String japaneseName, JLPTLevel jlptLevel, Long id);

    @Query("SELECT MAX(t.orderIndex) FROM TopicEntity t")
    Double getMaxOrderIndex();
}
