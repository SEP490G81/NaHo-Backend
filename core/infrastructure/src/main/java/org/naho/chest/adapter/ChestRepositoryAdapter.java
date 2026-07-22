package org.naho.chest.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.chest.mapper.ChestEntityMapper;
import org.naho.chest.model.Chest;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.chest.repository.ChestJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChestRepositoryAdapter implements ChestRepositoryPort {

    private final ChestJpaRepository chestJpaRepository;
    private final ChestEntityMapper chestEntityMapper;

    @Override
    public Optional<Chest> findById(Long id) {
        return chestJpaRepository
                .findById(id)
                .map(chestEntityMapper::entityToDomain);
    }

    @Override
    public List<Chest> findAllByIdIn(Collection<Long> ids) {
        return chestJpaRepository.findAllByIdIn(ids).stream()
                .map(chestEntityMapper::entityToDomain)
                .toList();
    }
}
