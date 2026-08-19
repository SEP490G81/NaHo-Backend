package org.naho.grammar.usecase;

import org.naho.grammar.command.SearchGrammarCommand;
import org.naho.grammar.port.in.SearchGrammarInputPort;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.grammar.result.GrammarResult;
import org.naho.pagination.PageData;

public class SearchGrammarUseCase implements SearchGrammarInputPort {

    private final GrammarRepositoryPort grammarRepositoryPort;

    public SearchGrammarUseCase(GrammarRepositoryPort grammarRepositoryPort) {
        this.grammarRepositoryPort = grammarRepositoryPort;
    }

    @Override
    public PageData<GrammarResult> searchGrammars(SearchGrammarCommand command) {
        var pageData = grammarRepositoryPort.searchByKeyword(command.keyword(), command.page(), command.size());

        var resultList = pageData.getData().stream()
                .map(g -> new GrammarResult(
                        g.getId(),
                        g.getReading(),
                        g.getJapanese(),
                        g.getVietnameseMeaningText(),
                        g.getEnglishMeaningText()
                ))
                .toList();

        return new PageData<>(
                resultList,
                pageData.getPageMeta()
        );
    }
}
