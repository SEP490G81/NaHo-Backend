package org.naho.question.port.in;

import org.naho.question.command.UpdateSpeakingQuestionCommand;
import org.naho.question.result.UpdateSpeakingQuestionResult;

public interface UpdateSpeakingQuestionInputPort {
    UpdateSpeakingQuestionResult updateSpeakingQuestion(UpdateSpeakingQuestionCommand command);
}
