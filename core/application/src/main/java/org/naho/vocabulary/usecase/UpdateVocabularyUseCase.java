package org.naho.vocabulary.usecase;

import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.command.UpdateVocabularyCommand;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.in.UpdateVocabularyInputPort;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;
import org.naho.vocabulary.result.VocabularyResult;

public class UpdateVocabularyUseCase implements UpdateVocabularyInputPort {

    private final VocabularyRepositoryPort vocabularyRepositoryPort;

    public UpdateVocabularyUseCase(VocabularyRepositoryPort vocabularyRepositoryPort) {
        this.vocabularyRepositoryPort = vocabularyRepositoryPort;
    }

    @Override
    public VocabularyResult updateVocabulary(UpdateVocabularyCommand command) {
        vocabularyRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        VocabularyErrorCode.VOCABULARY_NOT_FOUND,
                        "vocabulary.not.found" // Assuming message key
                ));

        Vocabulary updatedVocabulary = Vocabulary.builder()
                .id(command.id())
                .reading(command.reading())
                .japanese(command.japanese())
                .vietnameseMeaningText(command.vietnameseMeaningText())
                .englishMeaningText(command.englishMeaningText())
                .build();

        Vocabulary saved = vocabularyRepositoryPort.save(updatedVocabulary);

        return new VocabularyResult(
                saved.getId(),
                saved.getReading(),
                saved.getJapanese(),
                saved.getVietnameseMeaningText(),
                saved.getEnglishMeaningText()
        );
    }
}
