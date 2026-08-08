package org.naho.vocabulary.port.in;

import org.naho.vocabulary.result.VocabularyQuizResult;

import java.util.List;

public interface GetRandomVocabularyQuizInputPort {
    List<VocabularyQuizResult> getRandomQuiz(int count);
}
