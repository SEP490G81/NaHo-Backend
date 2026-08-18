package org.naho.speech.llm.question.port.out;

import org.naho.speech.llm.model.question.AiFeedback;

public interface AiFeedbackRepositoryPort {
    AiFeedback createNew(AiFeedback feedback);
}
