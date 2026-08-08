package org.naho.quote.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.quote.entity.QuoteEntity;
import org.naho.quote.mapper.QuoteEntityMapper;
import org.naho.quote.model.Quote;
import org.naho.quote.port.out.QuotePort;
import org.naho.quote.repository.QuoteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QuoteRepositoryAdapter implements QuotePort {

    private final QuoteJpaRepository quoteJpaRepository;
    private final QuoteEntityMapper quoteEntityMapper;

    @Override
    public Optional<Quote> findRandomQuote() {
        return quoteJpaRepository.findRandomQuote()
                .map(quoteEntityMapper::toDomain);
    }

    @Override
    public List<Quote> saveAll(List<Quote> quotes) {
        List<QuoteEntity> entities = quoteEntityMapper.toEntityList(quotes);
        List<QuoteEntity> saved = quoteJpaRepository.saveAll(entities);
        return quoteEntityMapper.toDomainList(saved);
    }

    @Override
    public boolean exists() {
        return quoteJpaRepository.existsBy();
    }
}
