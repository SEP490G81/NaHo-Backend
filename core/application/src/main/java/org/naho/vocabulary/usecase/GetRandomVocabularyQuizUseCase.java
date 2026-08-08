package org.naho.vocabulary.usecase;

import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.in.GetRandomVocabularyQuizInputPort;
import org.naho.vocabulary.port.out.RandomVocabularyPort;
import org.naho.vocabulary.result.VocabularyQuizResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetRandomVocabularyQuizUseCase implements GetRandomVocabularyQuizInputPort {

    private static final String[] OPTION_LABELS = {"A", "B", "C", "D"};
    private final RandomVocabularyPort randomVocabularyPort;

    public GetRandomVocabularyQuizUseCase(RandomVocabularyPort randomVocabularyPort) {
        this.randomVocabularyPort = randomVocabularyPort;
    }

    @Override
    public List<VocabularyQuizResult> getRandomQuiz(int count) {
        int safeCount = count <= 0 ? 1 : count;
        List<VocabularyQuizResult> results = new ArrayList<>();

        for (int i = 0; i < safeCount; i++) {
            Vocabulary target = randomVocabularyPort.findRandomVocabulary()
                    .orElseThrow(() -> new ApplicationException(
                            VocabularyErrorCode.VOCABULARY_NOT_FOUND,
                            VocabularyQuestionDetailMessageKey.VOCABULARY_NOT_FOUND
                    ));

            String correctMeaning = target.getVietnameseMeaningText();
            List<String> distractors = randomVocabularyPort.findRandomDistractorMeanings(
                    target.getId(),
                    correctMeaning,
                    3
            );

            List<String> optionTexts = new ArrayList<>();
            if (correctMeaning != null) {
                optionTexts.add(correctMeaning);
            }
            optionTexts.addAll(distractors);

            Collections.shuffle(optionTexts);

            List<VocabularyQuizResult.QuizOptionResult> optionResults = new ArrayList<>();
            String correctOptionId = "A";

            for (int j = 0; j < optionTexts.size(); j++) {
                String label = j < OPTION_LABELS.length ? OPTION_LABELS[j] : String.valueOf(j + 1);
                String text = optionTexts.get(j);
                optionResults.add(new VocabularyQuizResult.QuizOptionResult(label, text));

                if (text.equals(correctMeaning)) {
                    correctOptionId = label;
                }
            }

            results.add(new VocabularyQuizResult(
                    target.getId(),
                    target.getJapanese(),
                    target.getReading(),
                    optionResults,
                    correctOptionId
            ));
        }

        return results;
    }
}
