package org.naho.speech.topic.port.out;

import org.naho.speech.model.Topic;
import org.naho.user.type.JLPTLevel;

import java.util.Optional;

public interface TopicRepositoryPort {
    Topic save(Topic topic);

    boolean existsByNameAndJlptLevel(String name, JLPTLevel jlptLevel);

    boolean existsByNameAndJlptLevelExcludeId(String name, JLPTLevel jlptLevel, Long id);

    Double getMaxOrderIndex();

    Optional<Topic> findById(Long id);

    void deleteById(Long id);
}
