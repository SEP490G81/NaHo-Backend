package org.naho.vocabulary.usecase;

import org.naho.vocabulary.command.CreateVocabularyCommand;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.in.CreateVocabularyInputPort;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;
import org.naho.vocabulary.result.VocabularyResult;

public class CreateVocabularyUseCase implements CreateVocabularyInputPort {

    private final VocabularyRepositoryPort vocabularyRepositoryPort;

    public CreateVocabularyUseCase(VocabularyRepositoryPort vocabularyRepositoryPort) {
        this.vocabularyRepositoryPort = vocabularyRepositoryPort;
    }

    @Override
    public VocabularyResult createVocabulary(CreateVocabularyCommand command) {
        Vocabulary vocabulary = Vocabulary.builder()
                .reading(command.reading())
                .japanese(command.japanese())
                .vietnameseMeaningText(command.vietnameseMeaningText())
                .englishMeaningText(command.englishMeaningText())
                .build();

        Vocabulary saved = vocabularyRepositoryPort.save(vocabulary);

        return new VocabularyResult(
                saved.getId(),
                saved.getReading(),
                saved.getJapanese(),
                saved.getVietnameseMeaningText(),
                saved.getEnglishMeaningText()
        );
    }
}
