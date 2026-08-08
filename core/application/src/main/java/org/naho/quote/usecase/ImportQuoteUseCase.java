package org.naho.quote.usecase;

import org.naho.i18n.message.quote.QuoteDetailMessageKey;
import org.naho.quote.exception.QuoteErrorCode;
import org.naho.quote.model.Quote;
import org.naho.quote.port.in.ImportQuoteInputPort;
import org.naho.quote.port.out.QuoteExcelParserPort;
import org.naho.quote.port.out.QuotePort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.io.InputStream;
import java.util.List;

public class ImportQuoteUseCase implements ImportQuoteInputPort {

    private final QuoteExcelParserPort quoteExcelParserPort;
    private final QuotePort quotePort;
    private final TransactionPort transactionPort;

    public ImportQuoteUseCase(QuoteExcelParserPort quoteExcelParserPort,
                              QuotePort quotePort,
                              TransactionPort transactionPort) {
        this.quoteExcelParserPort = quoteExcelParserPort;
        this.quotePort = quotePort;
        this.transactionPort = transactionPort;
    }

    @Override
    public void importQuote(InputStream inputStream) {
        transactionPort.execute(() -> {
            List<Quote> quotes = quoteExcelParserPort.parseQuoteExcel(inputStream);

            if (quotes == null || quotes.isEmpty()) {
                throw new ApplicationException(
                        QuoteErrorCode.QUOTE_IMPORT_EMPTY,
                        QuoteDetailMessageKey.QUOTE_IMPORT_EMPTY
                );
            }

            quotePort.saveAll(quotes);
            return null;
        });
    }
}
