package org.naho.question.usecase;

import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.question.command.UpdateVocabularyQuestionCommand;
import org.naho.question.exception.VocabularyQuestionErrorCode;
import org.naho.question.model.VocabularyQuestion;
import org.naho.question.port.in.UpdateVocabularyQuestionInputPort;
import org.naho.question.port.out.VocabularyQuestionRepositoryPort;
import org.naho.question.result.UpdateVocabularyQuestionResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;
import org.naho.vocabulary.result.VocabularyResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UpdateVocabularyQuestionUseCase implements UpdateVocabularyQuestionInputPort {

    private final VocabularyQuestionRepositoryPort vocabularyQuestionRepositoryPort;
    private final VocabularyRepositoryPort vocabularyRepositoryPort;
    private final TransactionPort transactionPort;

    public UpdateVocabularyQuestionUseCase(
            VocabularyQuestionRepositoryPort vocabularyQuestionRepositoryPort,
            VocabularyRepositoryPort vocabularyRepositoryPort,
            TransactionPort transactionPort
    ) {
        this.vocabularyQuestionRepositoryPort = vocabularyQuestionRepositoryPort;
        this.vocabularyRepositoryPort = vocabularyRepositoryPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public UpdateVocabularyQuestionResult updateVocabularyQuestion(UpdateVocabularyQuestionCommand command) {
        return transactionPort.execute(() -> {
            // 1. Verify Question exists
            Optional<VocabularyQuestion> questionOpt = vocabularyQuestionRepositoryPort.findById(command.id());
            if (questionOpt.isEmpty()) {
                throw new ApplicationException(
                        VocabularyQuestionErrorCode.VOCABULARY_QUESTION_NOT_FOUND,
                        VocabularyQuestionDetailMessageKey.VOCABULARY_QUESTION_NOT_FOUND,
                        command.id()
                );
            }

            VocabularyQuestion vocabularyQuestion = questionOpt.get();

            // 2. Nested Upsert for Vocabularies
            List<Vocabulary> finalVocabularies = new ArrayList<>();
            if (command.vocabularies() != null) {
                for (var vocabCommand : command.vocabularies()) {
                    if (vocabCommand.id() != null) {
                        // Use existing vocabulary
                        vocabularyRepositoryPort.findById(vocabCommand.id())
                                .ifPresent(finalVocabularies::add);
                    } else {
                        // Create new vocabulary
                        Vocabulary newVocab = Vocabulary.builder()
                                .reading(vocabCommand.reading())
                                .japanese(vocabCommand.japanese())
                                .vietnameseMeaningText(vocabCommand.vietnameseMeaningText())
                                .englishMeaningText(vocabCommand.englishMeaningText())
                                .build();
                        Vocabulary savedVocab = vocabularyRepositoryPort.save(newVocab);
                        finalVocabularies.add(savedVocab);
                    }
                }
            }

            // 3. Update Domain Model
            vocabularyQuestion.update(finalVocabularies);

            // 4. Save and Return
            VocabularyQuestion updatedVocabularyQuestion = vocabularyQuestionRepositoryPort.save(vocabularyQuestion);

            List<VocabularyResult> vocabularyResults = updatedVocabularyQuestion.getVocabularies().stream()
                    .map(v -> new VocabularyResult(
                            v.getId(),
                            v.getReading(),
                            v.getJapanese(),
                            v.getVietnameseMeaningText(),
                            v.getEnglishMeaningText()
                    )).toList();

            return new UpdateVocabularyQuestionResult(
                    updatedVocabularyQuestion.getId(),
                    vocabularyResults
            );
        });
    }
}
