package org.naho.speech.llm.port.out;

public interface AiAnalysisPort {
    String analyzeSpeaking(String topic, String question, String studentTranscript, String azureWordFeedbackJson);
}
