package org.naho.quote.mapper;

import org.mapstruct.Mapper;
import org.naho.quote.entity.QuoteEntity;
import org.naho.quote.model.Quote;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuoteEntityMapper {

    Quote toDomain(QuoteEntity entity);

    QuoteEntity toEntity(Quote domain);

    List<Quote> toDomainList(List<QuoteEntity> entities);

    List<QuoteEntity> toEntityList(List<Quote> domains);
}
