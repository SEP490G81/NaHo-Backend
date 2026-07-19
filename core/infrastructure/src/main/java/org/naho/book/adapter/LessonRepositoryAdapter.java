package org.naho.book.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.book.mapper.LessonEntityMapper;
import org.naho.book.model.Lesson;
import org.naho.book.mybatis.LessonQueryMapper;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.repository.LessonJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LessonRepositoryAdapter implements LessonRepositoryPort {
    private final LessonJpaRepository lessonJpaRepository;
    private final LessonEntityMapper lessonEntityMapper;
    private final LessonQueryMapper lessonQueryMapper;

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

    @Override
    public Optional<Lesson> findBySpeakingQuestionId(Long speakingQuestionId) {
        return lessonQueryMapper
                .findBySpeakingQuestionId(speakingQuestionId)
                .map(lessonEntityMapper::entityToDomain);
    }
}
