package org.naho.question.port.in;

import org.naho.question.command.CompleteVocabularyQuestionCommand;

public interface CompleteVocabularyQuestionInputPort {
    void completeVocabularyQuestion(CompleteVocabularyQuestionCommand command);
}
