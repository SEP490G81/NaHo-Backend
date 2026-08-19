package org.naho.vocabulary.usecase;

import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.port.in.DeleteVocabularyInputPort;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;

public class DeleteVocabularyUseCase implements DeleteVocabularyInputPort {

    private final VocabularyRepositoryPort vocabularyRepositoryPort;

    public DeleteVocabularyUseCase(VocabularyRepositoryPort vocabularyRepositoryPort) {
        this.vocabularyRepositoryPort = vocabularyRepositoryPort;
    }

    @Override
    public void deleteVocabulary(Long id) {
        vocabularyRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        VocabularyErrorCode.VOCABULARY_NOT_FOUND,
                        "vocabulary.not.found"
                ));

        vocabularyRepositoryPort.deleteById(id);
    }
}
