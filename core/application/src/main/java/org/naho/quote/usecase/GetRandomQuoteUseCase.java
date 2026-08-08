package org.naho.quote.usecase;

import org.naho.i18n.message.quote.QuoteDetailMessageKey;
import org.naho.quote.exception.QuoteErrorCode;
import org.naho.quote.model.Quote;
import org.naho.quote.port.in.GetRandomQuoteInputPort;
import org.naho.quote.port.out.QuotePort;
import org.naho.quote.result.QuoteResult;
import org.naho.shared.exception.ApplicationException;

public class GetRandomQuoteUseCase implements GetRandomQuoteInputPort {

    private final QuotePort quotePort;

    public GetRandomQuoteUseCase(QuotePort quotePort) {
        this.quotePort = quotePort;
    }

    @Override
    public QuoteResult getRandomQuote() {
        Quote quote = quotePort.findRandomQuote()
                .orElseThrow(() -> new ApplicationException(
                        QuoteErrorCode.QUOTE_NOT_FOUND,
                        QuoteDetailMessageKey.QUOTE_NOT_FOUND
                ));
        return QuoteResult.from(quote);
    }
}
