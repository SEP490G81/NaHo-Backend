package org.naho.question.port.in;

import org.naho.question.command.LearningPathNodeCommand;
import org.naho.question.result.VocabulariesOfQuestionResult;

public interface SearchVocabulariesOfQuestionInputPort {
    VocabulariesOfQuestionResult getVocabularyListOfQuestion(LearningPathNodeCommand learningPathNodeCommand);

}
