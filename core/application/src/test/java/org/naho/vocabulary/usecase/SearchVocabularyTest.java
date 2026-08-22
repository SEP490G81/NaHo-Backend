package org.naho.vocabulary.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.pagination.PageData;
import org.naho.pagination.PageMeta;
import org.naho.question.model.Vocabulary;
import org.naho.vocabulary.command.SearchVocabularyCommand;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;
import org.naho.vocabulary.result.VocabularyResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchVocabularyTest {

    @Mock
    private VocabularyRepositoryPort vocabularyRepositoryPort;

    @InjectMocks
    private SearchVocabularyUseCase searchVocabularyUseCase;

    @Test
    @DisplayName("UTCID01 - Tìm kiếm từ vựng theo từ khóa thành công")
    void UTCID01_SearchVocabularies_Success() {
        SearchVocabularyCommand command = new SearchVocabularyCommand("あい", 1, 10);
        Vocabulary vocab = Vocabulary.builder().id(1L).reading("あい").japanese("愛").build();
        PageMeta pageMeta = PageMeta.builder().currentPage(1).pageSize(10).totalElements(1L).totalPages(1).hasNext(false).hasPrevious(false).build();
        PageData<Vocabulary> pageData = new PageData<>(List.of(vocab), pageMeta);

        when(vocabularyRepositoryPort.searchByKeyword("あい", 1, 10)).thenReturn(pageData);

        PageData<VocabularyResult> result = searchVocabularyUseCase.searchVocabularies(command);

        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals("愛", result.getData().get(0).japanese());
    }
}
