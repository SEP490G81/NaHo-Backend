package org.naho.question.port.in;

import org.naho.question.command.FindSpeakingQuestionCommand;
import org.naho.question.result.SpeakingQuestionResult;

public interface GetSpeakingQuestionInputPort {
    SpeakingQuestionResult findById(FindSpeakingQuestionCommand command);
}
