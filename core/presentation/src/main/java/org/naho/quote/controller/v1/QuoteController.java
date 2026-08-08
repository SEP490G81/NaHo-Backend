package org.naho.quote.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.quote.QuoteDetailMessageKey;
import org.naho.quote.dto.mapper.QuoteResponseMapper;
import org.naho.quote.dto.response.QuoteResponse;
import org.naho.quote.port.in.GetRandomQuoteInputPort;
import org.naho.quote.result.QuoteResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quote")
@RequiredArgsConstructor
public class QuoteController {

    private final GetRandomQuoteInputPort getRandomQuoteInputPort;
    private final QuoteResponseMapper quoteResponseMapper;

    @GetMapping
    @ApiResponseMessage(message = QuoteDetailMessageKey.QUOTE_GET_SUCCESS)
    public ResponseEntity<QuoteResponse> getRandomQuote() {
        QuoteResult result = getRandomQuoteInputPort.getRandomQuote();
        QuoteResponse response = quoteResponseMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }
}
