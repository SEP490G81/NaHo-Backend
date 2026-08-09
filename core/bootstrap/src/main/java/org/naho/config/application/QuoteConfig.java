package org.naho.config.application;

import org.naho.quote.port.in.GetRandomQuoteInputPort;
import org.naho.quote.port.in.ImportQuoteInputPort;
import org.naho.quote.port.out.QuoteExcelParserPort;
import org.naho.quote.port.out.QuotePort;
import org.naho.quote.usecase.GetRandomQuoteUseCase;
import org.naho.quote.usecase.ImportQuoteUseCase;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuoteConfig {

    @Bean
    public GetRandomQuoteInputPort getRandomQuoteInputPort(QuotePort quotePort) {
        return new GetRandomQuoteUseCase(quotePort);
    }

    @Bean
    public ImportQuoteInputPort importQuoteInputPort(
            QuoteExcelParserPort quoteExcelParserPort,
            QuotePort quotePort,
            TransactionPort transactionPort
    ) {
        return new ImportQuoteUseCase(quoteExcelParserPort, quotePort, transactionPort);
    }
}
