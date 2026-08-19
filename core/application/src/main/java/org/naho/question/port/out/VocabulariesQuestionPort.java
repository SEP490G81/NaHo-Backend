package org.naho.question.port.out;

import org.naho.question.model.Vocabulary;

import java.util.List;

public interface VocabulariesQuestionPort {
    List<Vocabulary> findVocabularyListOfObjective(Long objectiveId);

    List<Vocabulary> findVocabularyListOfTopic(Long topicId);
}
