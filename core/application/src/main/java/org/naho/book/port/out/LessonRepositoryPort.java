package org.naho.book.port.out;

import org.naho.book.model.Lesson;

import java.util.List;
import java.util.Optional;

public interface LessonRepositoryPort {
    List<Lesson> findByTopicId(Long topicId);

    Optional<Lesson> findById(Long id);

    Optional<Lesson> findBySpeakingQuestionId(Long speakingQuestionId);
}
