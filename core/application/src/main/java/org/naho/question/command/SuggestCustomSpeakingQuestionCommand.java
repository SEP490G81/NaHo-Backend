package org.naho.question.command;

public record SuggestCustomSpeakingQuestionCommand(
        String hintVi,
        String category
) {}
