package org.naho.book.port.out;

import org.naho.book.model.Topic;
import java.util.Optional;

public interface TopicRepositoryPort {
    Topic save(Topic topic);

    boolean existsByJapaneseNameAndBookId(String name, Long bookId);

    boolean existsByJapaneseNameAndBookIdExcludeId(String name, Long bookId, Long id);

    Double getMaxOrderIndex();

    Optional<Topic> findById(Long id);

    Optional<Topic> findByObjectiveId(Long objectiveId);

    void deleteById(Long id);
}
