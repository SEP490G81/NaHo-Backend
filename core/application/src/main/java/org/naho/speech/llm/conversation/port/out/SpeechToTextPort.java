package org.naho.speech.llm.conversation.port.out;

import org.naho.speech.llm.conversation.result.SpeechToTextResult;

public interface SpeechToTextPort {
    SpeechToTextResult transcribeAndAssess(byte[] audioBytes, double duration, String referenceText, Long userId);
}
