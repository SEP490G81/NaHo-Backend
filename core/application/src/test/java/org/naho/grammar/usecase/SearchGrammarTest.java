package org.naho.grammar.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.grammar.command.SearchGrammarCommand;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.grammar.result.GrammarResult;
import org.naho.pagination.PageData;
import org.naho.pagination.PageMeta;
import org.naho.question.model.Grammar;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchGrammarTest {

    @Mock
    private GrammarRepositoryPort grammarRepositoryPort;

    @InjectMocks
    private SearchGrammarUseCase searchGrammarUseCase;

    @Test
    @DisplayName("UTCID01 - Tìm kiếm ngữ pháp theo từ khóa thành công")
    void UTCID01_SearchGrammars_Success() {
        SearchGrammarCommand command = new SearchGrammarCommand("です", 1, 10);
        Grammar grammar = Grammar.builder().id(1L).reading("です").japanese("です").build();
        PageMeta pageMeta = PageMeta.builder().currentPage(1).pageSize(10).totalElements(1L).totalPages(1).hasNext(false).hasPrevious(false).build();
        PageData<Grammar> pageData = new PageData<>(List.of(grammar), pageMeta);

        when(grammarRepositoryPort.searchByKeyword("です", 1, 10)).thenReturn(pageData);

        PageData<GrammarResult> result = searchGrammarUseCase.searchGrammars(command);

        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals("です", result.getData().get(0).japanese());
    }
}
