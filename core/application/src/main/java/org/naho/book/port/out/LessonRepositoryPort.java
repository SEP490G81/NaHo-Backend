package org.naho.book.port.out;

import org.naho.book.model.Lesson;

import java.util.List;

public interface LessonRepositoryPort {
    List<Lesson> findByTopicId(Long topicId);

}
