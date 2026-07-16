package org.naho.question.port.out;

import org.naho.question.model.Chest;

import java.util.Optional;

public interface ChestRepositoryPort {
    Optional<Chest> findById(Long id);
}
