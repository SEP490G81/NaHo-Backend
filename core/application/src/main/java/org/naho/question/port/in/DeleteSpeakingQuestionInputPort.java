package org.naho.question.port.in;

import org.naho.question.command.DeleteSpeakingQuestionCommand;

public interface DeleteSpeakingQuestionInputPort {
    void deleteSpeakingQuestion(DeleteSpeakingQuestionCommand command);
}
