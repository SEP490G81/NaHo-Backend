package org.naho.vocabulary.port.in;

import org.naho.pagination.PageData;
import org.naho.vocabulary.command.SearchVocabularyCommand;
import org.naho.vocabulary.result.VocabularyResult;

public interface SearchVocabularyInputPort {
    PageData<VocabularyResult> searchVocabularies(SearchVocabularyCommand command);
}
