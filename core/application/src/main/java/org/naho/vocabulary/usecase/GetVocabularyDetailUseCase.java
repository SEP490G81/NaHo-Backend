package org.naho.vocabulary.usecase;

import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.in.GetVocabularyDetailInputPort;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;
import org.naho.vocabulary.result.VocabularyResult;

public class GetVocabularyDetailUseCase implements GetVocabularyDetailInputPort {

    private final VocabularyRepositoryPort vocabularyRepositoryPort;

    public GetVocabularyDetailUseCase(VocabularyRepositoryPort vocabularyRepositoryPort) {
        this.vocabularyRepositoryPort = vocabularyRepositoryPort;
    }

    @Override
    public VocabularyResult getVocabularyDetail(Long id) {
        Vocabulary vocabulary = vocabularyRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        VocabularyErrorCode.VOCABULARY_NOT_FOUND,
                        "vocabulary.not.found"
                ));

        return new VocabularyResult(
                vocabulary.getId(),
                vocabulary.getReading(),
                vocabulary.getJapanese(),
                vocabulary.getVietnameseMeaningText(),
                vocabulary.getEnglishMeaningText()
        );
    }
}
