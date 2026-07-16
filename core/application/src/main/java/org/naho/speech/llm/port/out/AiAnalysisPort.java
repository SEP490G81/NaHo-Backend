package org.naho.speech.llm.port.out;

public interface AiAnalysisPort {
    record Context(
        String curriculum,
        String level,
        String stt,
        String topic,
        String lesson,
        String canDoObjective,
        String grammarFocus,
        String vocabularyFocus,
        String question,
        double accuracy,
        double fluency,
        double completeness,
        double prosody,
        String studentTranscript
    ) {}

    String analyzeSpeaking(Context context);
}

