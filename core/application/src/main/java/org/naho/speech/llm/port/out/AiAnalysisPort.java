package org.naho.speech.llm.port.out;

import org.naho.speech.llm.command.ContextCommand;

public interface AiAnalysisPort {
    String analyzeSpeaking(ContextCommand context);
}

