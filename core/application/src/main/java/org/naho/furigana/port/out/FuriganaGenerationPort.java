package org.naho.furigana.port.out;

import org.naho.furigana.model.FuriganaText;

public interface FuriganaGenerationPort {
    FuriganaText generate(String text);
}
