package org.naho.topic.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.topic.entity.ObjectiveEntity;
import org.naho.topic.model.Objective;
import org.naho.topic.port.out.ObjectiveRepositoryPort;
import org.naho.topic.repository.ObjectiveJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ObjectiveRepositoryAdapter implements ObjectiveRepositoryPort {

    private final ObjectiveJpaRepository objectiveJpaRepository;

    @Override
    public boolean existsById(Long id) {
        return objectiveJpaRepository.existsById(id);
    }

    @Override
    public Optional<Objective> findById(Long id) {
        return objectiveJpaRepository.findById(id).map(entity -> Objective.builder()
                .id(entity.getId())
                .lessonId(entity.getLesson() != null ? entity.getLesson().getId() : null)
                .japaneseName(entity.getJapaneseName())
                .japaneseDescription(entity.getJapaneseDescription())
                .japaneseNameMarkup(entity.getJapaneseNameMarkup())
                .japaneseDescriptionMarkup(entity.getJapaneseDescriptionMarkup())
                .status(entity.getStatus())
                .orderIndex(entity.getOrderIndex())
                .build());
    }
}
