package org.naho.question.port.in;

import org.naho.question.command.ChangeSpeakingQuestionStatusCommand;

public interface ChangeSpeakingQuestionStatusInputPort {
    void changeSpeakingQuestionStatus(ChangeSpeakingQuestionStatusCommand command);
}
