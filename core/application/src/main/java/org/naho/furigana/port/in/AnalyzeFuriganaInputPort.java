package org.naho.furigana.port.in;

import org.naho.furigana.command.AnalyzeFuriganaCommand;
import org.naho.furigana.result.FuriganaResult;

public interface AnalyzeFuriganaInputPort {
    FuriganaResult analyze(AnalyzeFuriganaCommand command);
}
