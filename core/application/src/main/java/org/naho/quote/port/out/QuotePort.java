package org.naho.quote.port.out;

import org.naho.quote.model.Quote;

import java.util.List;
import java.util.Optional;

public interface QuotePort {
    Optional<Quote> findRandomQuote();

    List<Quote> saveAll(List<Quote> quotes);

    boolean exists();
}
