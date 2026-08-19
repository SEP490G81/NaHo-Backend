package org.naho.speech.llm.question.port.in;

import org.naho.speech.llm.question.result.AiFeedbackResult;

public interface CrudAiFeedbackInputPort {
    AiFeedbackResult findById(Long id);
}
