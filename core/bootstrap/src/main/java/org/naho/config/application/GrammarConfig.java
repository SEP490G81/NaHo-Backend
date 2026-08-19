package org.naho.config.application;

import org.naho.grammar.mapper.GrammarResultMapper;
import org.naho.grammar.port.in.ImportGrammarPort;
import org.naho.grammar.port.out.GrammarExcelParserPort;
import org.naho.grammar.usecase.ImportGrammarUseCase;
import org.naho.question.port.out.SaveGrammarPort;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrammarConfig {

    @Bean
    public GrammarResultMapper grammarResultMapper() {
        return new GrammarResultMapper();
    }

    @Bean
    public ImportGrammarPort importGrammarUseCase(
            GrammarExcelParserPort grammarExcelParserPort,
            SaveGrammarPort saveGrammarPort,
            TransactionPort transactionPort
    ) {
        return new ImportGrammarUseCase(grammarExcelParserPort, saveGrammarPort, transactionPort);
    }
}

