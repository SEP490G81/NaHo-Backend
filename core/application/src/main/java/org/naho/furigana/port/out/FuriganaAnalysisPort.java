package org.naho.furigana.port.out;

import org.naho.furigana.model.FuriganaText;

public interface FuriganaAnalysisPort {
    FuriganaText analyze(String text);
}
