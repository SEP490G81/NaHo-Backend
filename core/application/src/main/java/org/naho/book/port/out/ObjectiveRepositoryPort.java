package org.naho.book.port.out;

import org.naho.book.model.Objective;

import java.util.List;
import java.util.Optional;

public interface ObjectiveRepositoryPort {
    boolean existsById(Long id);

    Optional<Objective> findById(Long id);

    Optional<Objective> findBySpeakingQuestionId(Long speakingQuestionId);

    List<Objective> findByLessonId(Long lessonId);

    void save(Objective objective);
}
