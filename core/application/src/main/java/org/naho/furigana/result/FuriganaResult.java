package org.naho.furigana.result;

import java.util.List;

public record FuriganaResult(
        String originalText,
        List<FuriganaTokenResult> tokens,
        String fullFurigana
) {
}
