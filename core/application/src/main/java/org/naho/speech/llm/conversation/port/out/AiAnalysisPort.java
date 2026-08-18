package org.naho.speech.llm.conversation.port.out;

import org.naho.speech.llm.conversation.command.ContextCommand;

public interface AiAnalysisPort {
    String analyzeSpeaking(ContextCommand context);
}

