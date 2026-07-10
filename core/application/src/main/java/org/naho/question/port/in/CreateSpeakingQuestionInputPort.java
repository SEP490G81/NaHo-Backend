package org.naho.question.port.in;

import org.naho.question.command.CreateSpeakingQuestionCommand;
import org.naho.question.result.CreateSpeakingQuestionResult;

public interface CreateSpeakingQuestionInputPort {
    CreateSpeakingQuestionResult createSpeakingQuestion(CreateSpeakingQuestionCommand command);
}
