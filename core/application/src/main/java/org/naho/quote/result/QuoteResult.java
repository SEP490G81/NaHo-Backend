package org.naho.quote.result;

import org.naho.quote.model.Quote;

public record QuoteResult(
        Long id,
        String kanji,
        String hiragana,
        String romaji,
        String translation,
        String kanjiDetail
) {
    public static QuoteResult from(Quote quote) {
        if (quote == null) {
            return null;
        }
        return new QuoteResult(
                quote.getId(),
                quote.getKanji(),
                quote.getHiragana(),
                quote.getRomaji(),
                quote.getTranslation(),
                quote.getKanjiDetail()
        );
    }
}
