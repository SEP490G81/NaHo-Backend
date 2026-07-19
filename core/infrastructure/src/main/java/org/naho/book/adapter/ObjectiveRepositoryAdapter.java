package org.naho.book.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.book.mapper.ObjectiveEntityMapper;
import org.naho.book.model.Objective;
import org.naho.book.mybatis.ObjectiveQueryMapper;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.repository.ObjectiveJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ObjectiveRepositoryAdapter implements ObjectiveRepositoryPort {

    private final ObjectiveJpaRepository objectiveJpaRepository;
    private final ObjectiveEntityMapper objectiveEntityMapper;
    private final ObjectiveQueryMapper objectiveQueryMapper;

    @Override
    public boolean existsById(Long id) {
        return objectiveJpaRepository.existsById(id);
    }

    @Override
    public Optional<Objective> findBySpeakingQuestionId(Long speakingQuestionId) {
        return objectiveQueryMapper
                .findBySpeakingQuestionId(speakingQuestionId)
                .map(objectiveEntityMapper::entityToDomain);
    }

    @Override
    public Optional<Objective> findById(Long id) {
        return objectiveJpaRepository
                .findById(id)
                .map(objectiveEntityMapper::entityToDomain);
    }

    @Override
    public List<Objective> findByLessonId(Long lessonId) {
        return objectiveJpaRepository.findByLessonId(lessonId).stream()
                .map(objectiveEntityMapper::entityToDomain)
                .toList();
    }

}
