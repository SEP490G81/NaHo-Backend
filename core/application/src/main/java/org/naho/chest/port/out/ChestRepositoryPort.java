package org.naho.chest.port.out;

import org.naho.chest.model.Chest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChestRepositoryPort {
    Optional<Chest> findById(Long id);

    List<Chest> findAllByIdIn(Collection<Long> ids);
}
