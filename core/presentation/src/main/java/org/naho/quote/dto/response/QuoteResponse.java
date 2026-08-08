package org.naho.quote.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResponse {
    private Long id;
    private String kanji;
    private String hiragana;
    private String romaji;
    private String translation;
    private String kanjiDetail;
}
