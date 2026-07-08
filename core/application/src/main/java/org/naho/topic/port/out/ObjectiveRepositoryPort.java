package org.naho.topic.port.out;

import org.naho.topic.model.Objective;
import java.util.Optional;

public interface ObjectiveRepositoryPort {
    boolean existsById(Long id);
    Optional<Objective> findById(Long id);
}
