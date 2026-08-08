package org.naho.quote.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.quote.dto.response.QuoteResponse;
import org.naho.quote.result.QuoteResult;

@Mapper(componentModel = "spring")
public interface QuoteResponseMapper {

    QuoteResponse toResponse(QuoteResult result);
}
