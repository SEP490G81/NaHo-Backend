package org.naho.speech.llm.question.port.out;

import org.naho.speech.llm.model.question.AiFeedback;

import java.util.Optional;

public interface AiFeedbackRepositoryPort {
    AiFeedback createNew(AiFeedback feedback);

    Optional<AiFeedback> findById(Long id);
}
