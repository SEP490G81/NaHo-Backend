package org.naho.speech.llm.command;

public record ContextCommand(
        String curriculum,
        String level,
        String stt,
        String topic,
        String lesson,
        String canDoObjective,
        String grammarFocus,
        String vocabularyFocus,
        String questionTitle,
        String questionDescription,
        double accuracy,
        double fluency,
        double completeness,
        double overallPronunciation,
        String studentTranscript
) {
}
