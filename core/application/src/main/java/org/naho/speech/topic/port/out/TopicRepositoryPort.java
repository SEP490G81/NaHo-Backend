package org.naho.speech.topic.port.out;

import org.naho.topic.model.Topic;
import org.naho.user.type.JLPTLevel;

import java.util.Optional;

public interface TopicRepositoryPort {
    Topic save(Topic topic);

    boolean existsByJapaneseNameAndJlptLevel(String japaneseName, JLPTLevel jlptLevel);

    boolean existsByJapaneseNameAndJlptLevelExcludeId(String japaneseName, JLPTLevel jlptLevel, Long id);

    Double getMaxOrderIndex();

    Optional<Topic> findById(Long id);

    void deleteById(Long id);
}
