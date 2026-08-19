package org.naho.config.application;

import org.naho.grammar.port.in.*;
import org.naho.grammar.port.out.GrammarExcelParserPort;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.grammar.usecase.*;
import org.naho.question.port.out.SaveGrammarPort;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrammarConfig {

    @Bean
    public ImportGrammarPort importGrammarUseCase(
            GrammarExcelParserPort grammarExcelParserPort,
            SaveGrammarPort saveGrammarPort,
            TransactionPort transactionPort
    ) {
        return new ImportGrammarUseCase(grammarExcelParserPort, saveGrammarPort, transactionPort);
    }

    @Bean
    public CreateGrammarInputPort createGrammarInputPort(GrammarRepositoryPort port) {
        return new CreateGrammarUseCase(port);
    }

    @Bean
    public UpdateGrammarInputPort updateGrammarInputPort(GrammarRepositoryPort port) {
        return new UpdateGrammarUseCase(port);
    }

    @Bean
    public DeleteGrammarInputPort deleteGrammarInputPort(GrammarRepositoryPort port) {
        return new DeleteGrammarUseCase(port);
    }

    @Bean
    public GetGrammarDetailInputPort getGrammarDetailInputPort(GrammarRepositoryPort port) {
        return new GetGrammarDetailUseCase(port);
    }

    @Bean
    public SearchGrammarInputPort searchGrammarInputPort(GrammarRepositoryPort port) {
        return new SearchGrammarUseCase(port);
    }
}
