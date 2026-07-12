package org.naho.book.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.book.model.Lesson;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.repository.LessonJpaRepository;
import org.naho.book.mapper.LessonEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LessonRepositoryAdapter implements LessonRepositoryPort {
    private final LessonJpaRepository lessonJpaRepository;
    private final LessonEntityMapper lessonEntityMapper;

    @Override
    public List<Lesson> findByTopicId(Long topicId) {
        return lessonJpaRepository.findByTopicId(topicId).stream()
                .map(lessonEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public Optional<Lesson> findById(Long id) {
        return lessonJpaRepository.findById(id).map(lessonEntityMapper::entityToDomain);
    }

}
