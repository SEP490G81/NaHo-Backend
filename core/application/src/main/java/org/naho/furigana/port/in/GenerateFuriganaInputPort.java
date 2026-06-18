package org.naho.furigana.port.in;

import org.naho.furigana.command.GenerateFuriganaCommand;
import org.naho.furigana.result.FuriganaResult;

public interface GenerateFuriganaInputPort {
    FuriganaResult generateFurigana(GenerateFuriganaCommand command);
}
