package org.naho.question.port.in;

import org.naho.question.command.UpdateVocabularyQuestionCommand;
import org.naho.question.result.UpdateVocabularyQuestionResult;

public interface UpdateVocabularyQuestionInputPort {
    UpdateVocabularyQuestionResult updateVocabularyQuestion(UpdateVocabularyQuestionCommand command);
}
