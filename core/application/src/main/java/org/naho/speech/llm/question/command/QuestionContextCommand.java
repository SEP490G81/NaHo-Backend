package org.naho.speech.llm.question.command;

public record QuestionContextCommand(
        String curriculum,
        String level,
        String lessonOrder,
        String topic,
        String lesson,
        String canDoObjective,
        String grammarFocus,
        String vocabularyFocus,
        String questionTitle,
        String questionDescription,
        String learnerTranscript
) {
}
