package org.naho.question.usecase;

import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.command.UpdateSpeakingQuestionCommand;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.model.Grammar;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.in.UpdateSpeakingQuestionInputPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.result.UpdateSpeakingQuestionResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UpdateSpeakingQuestionUseCase implements UpdateSpeakingQuestionInputPort {

    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    private final VocabularyRepositoryPort vocabularyRepositoryPort;
    private final GrammarRepositoryPort grammarRepositoryPort;
    private final TransactionPort transactionPort;
    private final FuriganaGenerationPort furiganaGenerationPort;

    public UpdateSpeakingQuestionUseCase(
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            VocabularyRepositoryPort vocabularyRepositoryPort,
            GrammarRepositoryPort grammarRepositoryPort,
            TransactionPort transactionPort,
            FuriganaGenerationPort furiganaGenerationPort
    ) {
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
        this.vocabularyRepositoryPort = vocabularyRepositoryPort;
        this.grammarRepositoryPort = grammarRepositoryPort;
        this.transactionPort = transactionPort;
        this.furiganaGenerationPort = furiganaGenerationPort;
    }

    @Override
    public UpdateSpeakingQuestionResult updateSpeakingQuestion(UpdateSpeakingQuestionCommand command) {
        return transactionPort.execute(() -> {
            // 1. Verify Question exists
            Optional<SpeakingQuestion> questionOpt = speakingQuestionRepositoryPort.findById(command.id());
            if (questionOpt.isEmpty()) {
                throw new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND,
                        command.id()
                );
            }

            SpeakingQuestion speakingQuestion = questionOpt.get();

            // 2. Check update rule: Cannot update if already answered
            boolean isAnswered = speakingQuestionRepositoryPort.hasSpeakingQuestionBeenAnswered(command.id());
            if (isAnswered) {
                throw new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                        command.id()
                );
            }

            // 3. Generate markup from raw text
            String japaneseNameMarkup = furiganaGenerationPort.generateFuriganaMarkup(command.japaneseName());
            String descriptionMarkup = command.description() != null ? furiganaGenerationPort.generateFuriganaMarkup(command.description()) : null;
            String japaneseSampleAnswerMarkup = command.japaneseSampleAnswer() != null ? furiganaGenerationPort.generateFuriganaMarkup(command.japaneseSampleAnswer()) : null;

            // 4. Nested Upsert for Vocabularies
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

            // 5. Nested Upsert for Grammars
            List<Grammar> finalGrammars = new ArrayList<>();
            if (command.grammars() != null) {
                for (var grammarCommand : command.grammars()) {
                    if (grammarCommand.id() != null) {
                        // Use existing grammar
                        grammarRepositoryPort.findById(grammarCommand.id())
                                .ifPresent(finalGrammars::add);
                    } else {
                        // Create new grammar
                        Grammar newGrammar = Grammar.builder()
                                .reading(grammarCommand.reading())
                                .japanese(grammarCommand.japanese())
                                .vietnameseMeaningText(grammarCommand.vietnameseMeaningText())
                                .englishMeaningText(grammarCommand.englishMeaningText())
                                .build();
                        Grammar savedGrammar = grammarRepositoryPort.save(newGrammar);
                        finalGrammars.add(savedGrammar);
                    }
                }
            }

            // 6. Update Domain Model
            speakingQuestion.update(
                    command.japaneseName(),
                    japaneseNameMarkup,
                    command.vietnameseName(),
                    command.description(),
                    descriptionMarkup,
                    command.japaneseSampleAnswer(),
                    japaneseSampleAnswerMarkup,
                    command.vietnameseSampleAnswer(),
                    command.englishSampleAnswer(),
                    null, // Audio is null for now
                    finalVocabularies,
                    finalGrammars
            );

            // 7. Save and Return
            SpeakingQuestion updatedSpeakingQuestion = speakingQuestionRepositoryPort.save(speakingQuestion);

            return new UpdateSpeakingQuestionResult(
                    updatedSpeakingQuestion.getId(),
                    updatedSpeakingQuestion.getUserId(),
                    updatedSpeakingQuestion.getJapaneseName(),
                    updatedSpeakingQuestion.getJapaneseNameMarkup(),
                    updatedSpeakingQuestion.getVietnameseName(),
                    updatedSpeakingQuestion.getDescription(),
                    updatedSpeakingQuestion.getDescriptionMarkup(),
                    updatedSpeakingQuestion.getJapaneseSampleAnswer(),
                    updatedSpeakingQuestion.getJapaneseSampleAnswerMarkup(),
                    updatedSpeakingQuestion.getVietnameseSampleAnswer(),
                    updatedSpeakingQuestion.getEnglishSampleAnswer(),
                    updatedSpeakingQuestion.getStatus()
            );
        });
    }
}
