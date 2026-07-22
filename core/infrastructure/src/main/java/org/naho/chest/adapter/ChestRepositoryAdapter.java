package org.naho.chest.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.chest.model.Chest;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.chest.repository.ChestJpaRepository;
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
                .chestType(entity.getChestType())
                .description(entity.getDescription())
                .minPoint(entity.getMinPoint())
                .maxPoint(entity.getMaxPoint())
                .build());
    }
}
