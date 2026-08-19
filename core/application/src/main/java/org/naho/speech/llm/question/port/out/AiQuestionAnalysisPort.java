package org.naho.speech.llm.question.port.out;

import org.naho.speech.llm.question.command.QuestionContextCommand;

public interface AiQuestionAnalysisPort {
    String analyzeSpeaking(QuestionContextCommand context);
}
