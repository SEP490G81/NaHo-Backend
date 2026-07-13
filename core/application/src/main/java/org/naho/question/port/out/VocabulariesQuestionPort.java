package org.naho.question.port.out;

import org.naho.vocabulary.model.Vocabulary;

import java.util.List;

public interface VocabulariesQuestionPort {
    List<Vocabulary> findVocabularyListOfObjective(int objectiveId);

}
