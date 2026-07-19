package org.naho.question.usecase;


import org.naho.question.command.SuggestCustomSpeakingQuestionCommand;
import org.naho.question.port.in.SuggestCustomSpeakingQuestionInputPort;
import org.naho.question.result.SuggestCustomSpeakingQuestionResult;
import org.naho.speech.llm.port.out.AiChatPort;

import java.util.List;
import java.util.Map;

public class SuggestCustomSpeakingQuestionUseCase implements SuggestCustomSpeakingQuestionInputPort {

    private final AiChatPort aiChatPort;

    public SuggestCustomSpeakingQuestionUseCase(AiChatPort aiChatPort) {
        this.aiChatPort = aiChatPort;
    }

    @Override
    public SuggestCustomSpeakingQuestionResult suggestCustomSpeakingQuestion(SuggestCustomSpeakingQuestionCommand command) {
        String category = command.category();
        String contextPrompt = "";
        if (category != null && !category.isBlank()) {
            switch (category.toLowerCase()) {
                case "brse":
                    contextPrompt = "Use Bridge SE context. The language should be polite Keigo (Sonkeigo/Kenjougo/Teineigo) suitable for business communication with Japanese clients.";
                    break;
                case "it":
                    contextPrompt = "Use IT/Tech context. Include relevant IT technical terminology in Japanese and keep it professional.";
                    break;
                case "office":
                    contextPrompt = "Use general office/business context. The language should be standard polite business Japanese (Teineigo/Keigo) suitable for colleagues or management.";
                    break;
                case "daily":
                    contextPrompt = "Use daily conversational context. The language should be natural, casual or standard polite (Teineigo) Japanese suitable for daily life.";
                    break;
                default:
                    contextPrompt = "";
                    break;
            }
        }

        String systemPrompt = "You are a professional Japanese-Vietnamese translation assistant. " +
                "Translate or convert the Vietnamese hint/context into a natural, grammatically correct Japanese question. " +
                contextPrompt + " " +
                "Return ONLY the raw Japanese question text. Do NOT include any explanations, English, Vietnamese, Romaji, quotes, markdown formatting, or extra characters. Only return the Japanese question text itself.";

        String userPrompt = "Translate/convert this Vietnamese hint into a Japanese question: " + command.hintVi();

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        );

        String rawJp = aiChatPort.chatWithContext(messages);
        String questionJp = "";
        if (rawJp != null) {
            questionJp = rawJp.trim();
            if (questionJp.startsWith("\"") && questionJp.endsWith("\"")) {
                questionJp = questionJp.substring(1, questionJp.length() - 1).trim();
            }
            if (questionJp.startsWith("「") && questionJp.endsWith("」")) {
                questionJp = questionJp.substring(1, questionJp.length() - 1).trim();
            }
        }

        return new SuggestCustomSpeakingQuestionResult(questionJp);
    }
}
