package org.naho.furigana.mapper;

import org.naho.furigana.model.FuriganaText;
import org.naho.furigana.result.FuriganaResult;
import org.naho.furigana.result.FuriganaTokenResult;

import java.util.stream.Collectors;

public class FuriganaResultMapper {
    public FuriganaResult domainToResult(FuriganaText domain) {
        if (domain == null) return null;

        return new FuriganaResult(
                domain.getOriginalText(),
                domain.getTokens().stream()
                        .map(token -> new FuriganaTokenResult(
                                token.getKanji(),
                                token.getFurigana()
                        ))
                        .collect(Collectors.toList()),
                domain.getMarkupString()
        );
    }
}
