package org.naho.question.command;

public record SuggestCustomQuestionCommand(
        String hintVi,
        String category
) {}
