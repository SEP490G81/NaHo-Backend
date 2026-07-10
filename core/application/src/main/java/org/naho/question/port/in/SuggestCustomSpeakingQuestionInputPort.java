package org.naho.question.port.in;

import org.naho.question.command.SuggestCustomSpeakingQuestionCommand;
import org.naho.question.result.SuggestCustomSpeakingQuestionResult;

public interface SuggestCustomSpeakingQuestionInputPort {
    SuggestCustomSpeakingQuestionResult suggestCustomSpeakingQuestion(SuggestCustomSpeakingQuestionCommand command);
}
