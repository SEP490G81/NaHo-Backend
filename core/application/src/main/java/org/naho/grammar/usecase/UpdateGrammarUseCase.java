package org.naho.grammar.usecase;

import org.naho.grammar.command.UpdateGrammarCommand;
import org.naho.grammar.exception.GrammarErrorCode;
import org.naho.grammar.port.in.UpdateGrammarInputPort;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.grammar.result.GrammarResult;
import org.naho.question.model.Grammar;
import org.naho.shared.exception.ApplicationException;

public class UpdateGrammarUseCase implements UpdateGrammarInputPort {

    private final GrammarRepositoryPort grammarRepositoryPort;

    public UpdateGrammarUseCase(GrammarRepositoryPort grammarRepositoryPort) {
        this.grammarRepositoryPort = grammarRepositoryPort;
    }

    @Override
    public GrammarResult updateGrammar(UpdateGrammarCommand command) {
        grammarRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        GrammarErrorCode.GRAMMAR_NOT_FOUND,
                        "grammar.not.found" // Assuming message key
                ));

        Grammar updatedGrammar = Grammar.builder()
                .id(command.id())
                .reading(command.reading())
                .japanese(command.japanese())
                .vietnameseMeaningText(command.vietnameseMeaningText())
                .englishMeaningText(command.englishMeaningText())
                .build();

        Grammar saved = grammarRepositoryPort.save(updatedGrammar);

        return new GrammarResult(
                saved.getId(),
                saved.getReading(),
                saved.getJapanese(),
                saved.getVietnameseMeaningText(),
                saved.getEnglishMeaningText()
        );
    }
}
