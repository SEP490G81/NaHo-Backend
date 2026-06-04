package org.naho.speech.topic.port.out;

import org.naho.speech.model.Topic;
import org.naho.user.type.JLPTLevel;

public interface TopicRepositoryPort {
    Topic save(Topic topic);

    boolean existsByNameAndJlptLevel(String name, JLPTLevel jlptLevel);

    Double getMaxOrderIndex();
}
