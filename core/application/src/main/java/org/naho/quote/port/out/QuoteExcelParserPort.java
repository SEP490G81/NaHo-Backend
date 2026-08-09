package org.naho.quote.port.out;

import org.naho.quote.model.Quote;

import java.io.InputStream;
import java.util.List;

public interface QuoteExcelParserPort {
    List<Quote> parseQuoteExcel(InputStream inputStream);
}
