package org.naho.vocabulary.usecase;

import org.naho.pagination.PageData;
import org.naho.vocabulary.command.SearchVocabularyCommand;
import org.naho.vocabulary.port.in.SearchVocabularyInputPort;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;
import org.naho.vocabulary.result.VocabularyResult;

public class SearchVocabularyUseCase implements SearchVocabularyInputPort {

    private final VocabularyRepositoryPort vocabularyRepositoryPort;

    public SearchVocabularyUseCase(VocabularyRepositoryPort vocabularyRepositoryPort) {
        this.vocabularyRepositoryPort = vocabularyRepositoryPort;
    }

    @Override
    public PageData<VocabularyResult> searchVocabularies(SearchVocabularyCommand command) {
        var pageData = vocabularyRepositoryPort.searchByKeyword(command.keyword(), command.page(), command.size());

        var resultList = pageData.getData().stream()
                .map(v -> new VocabularyResult(
                        v.getId(),
                        v.getReading(),
                        v.getJapanese(),
                        v.getVietnameseMeaningText(),
                        v.getEnglishMeaningText()
                ))
                .toList();

        return new PageData<>(
                resultList,
                pageData.getPageMeta()
        );
    }
}
