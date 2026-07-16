package org.naho.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.question.model.Chest;
import org.naho.question.port.out.ChestRepositoryPort;
import org.naho.question.repository.ChestJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChestRepositoryAdapter implements ChestRepositoryPort {

    private final ChestJpaRepository chestJpaRepository;

    @Override
    public Optional<Chest> findById(Long id) {
        return chestJpaRepository.findById(id).map(entity -> Chest.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .point(entity.getPoint())
                .build());
    }
}
