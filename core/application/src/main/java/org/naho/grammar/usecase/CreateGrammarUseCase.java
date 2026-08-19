package org.naho.grammar.usecase;

import org.naho.grammar.command.CreateGrammarCommand;
import org.naho.grammar.port.in.CreateGrammarInputPort;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.grammar.result.GrammarResult;
import org.naho.question.model.Grammar;

public class CreateGrammarUseCase implements CreateGrammarInputPort {

    private final GrammarRepositoryPort grammarRepositoryPort;

    public CreateGrammarUseCase(GrammarRepositoryPort grammarRepositoryPort) {
        this.grammarRepositoryPort = grammarRepositoryPort;
    }

    @Override
    public GrammarResult createGrammar(CreateGrammarCommand command) {
        Grammar grammar = Grammar.builder()
                .reading(command.reading())
                .japanese(command.japanese())
                .vietnameseMeaningText(command.vietnameseMeaningText())
                .englishMeaningText(command.englishMeaningText())
                .build();

        Grammar saved = grammarRepositoryPort.save(grammar);

        return new GrammarResult(
                saved.getId(),
                saved.getReading(),
                saved.getJapanese(),
                saved.getVietnameseMeaningText(),
                saved.getEnglishMeaningText()
        );
    }
}
