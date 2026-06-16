package org.naho.topic.port.out;

import org.naho.topic.model.Topic;
import org.naho.user.type.JLPTLevel;

import java.util.Optional;

public interface TopicRepositoryPort {
    Topic save(Topic topic);

    boolean existsByJapaneseNameAndJlptLevel(String name, JLPTLevel jlptLevel);

    boolean existsByJapaneseNameAndJlptLevelExcludeId(String name, JLPTLevel jlptLevel, Long id);

    Double getMaxOrderIndex();

    Optional<Topic> findById(Long id);

    void deleteById(Long id);
}
