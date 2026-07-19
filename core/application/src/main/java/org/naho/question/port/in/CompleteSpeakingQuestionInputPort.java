package org.naho.question.port.in;

import org.naho.question.command.CompleteSpeakingQuestionCommand;

public interface CompleteSpeakingQuestionInputPort {
    void completeSpeakingQuestion(CompleteSpeakingQuestionCommand command);
}
