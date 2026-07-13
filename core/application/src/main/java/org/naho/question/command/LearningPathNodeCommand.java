package org.naho.question.command;

public record LearningPathNodeCommand(
        int id,
        String node_type,
        int objective_id,
        int vocabulary_question_id
) {
}
